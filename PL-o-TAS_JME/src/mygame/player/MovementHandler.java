/*
 * MovementHandler.java
 * Reescrito para trabajar con la nueva PlayerAnimation (JME 3.8).
 *
 * Cambios respecto a la versión anterior:
 *  - Se elimina animation.playAnimation() — no existía en PlayerAnimation.
 *    Ahora se usan animation.setLocomotion(), animation.startJump() y
 *    animation.landJump(), que son los métodos reales de la nueva clase.
 *
 *  - La máquina de estados se simplifica a dos estados (GROUNDED / AIRBORNE)
 *    porque PlayerAnimation ya gestiona internamente la transición
 *    JumpStart → Jumping → JumpEnd a través de Tweens.callMethod().
 *    El handler solo necesita saber cuándo despegar y cuándo aterrizar.
 *
 *  - Se corrige el doble cómputo de onGround: el valor filtrado ahora se
 *    calcula antes de usarlo en cualquier bloque de lógica.
 *
 *  - Se elimina el código duplicado: updateRotation() y setWalkDirection()
 *    se llaman una sola vez al final de update().
 *
 *  - Se elimina JUMP_END_DURATION (era 0.001 f, inútil). La duración de
 *    JumpEnd la determina el propio clip de animación, gestionado por
 *    PlayerAnimation.onJumpLanded() vía Tween.
 *
 *  - jumpInitiated se limpia correctamente: solo se consume cuando la
 *    máquina de estados lo procesa, no al final de cada frame.
 */
package mygame.player;

import com.jme3.bullet.control.CharacterControl;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;

/**
 * Gestiona el movimiento físico y coordina las animaciones del jugador.
 *
 * <h3>Responsabilidades</h3>
 * <ul>
 *   <li>Calcular la dirección de movimiento a partir del input y el yaw de cámara.</li>
 *   <li>Aplicar la dirección al CharacterControl (física).</li>
 *   <li>Detectar despegue y aterrizaje para notificar a PlayerAnimation.</li>
 *   <li>Actualizar la locomoción base (Idle / Walk) cuando el personaje está en suelo.</li>
 * </ul>
 *
 * <h3>División de responsabilidades con PlayerAnimation</h3>
 * <ul>
 *   <li>MovementHandler detecta <em>cuándo</em> saltar o aterrizar.</li>
 *   <li>PlayerAnimation sabe <em>cómo</em> animar cada fase del salto.</li>
 * </ul>
 */
public class MovementHandler {

    // -----------------------------------------------------------------------
    // Estado interno del salto (perspectiva del handler)
    // -----------------------------------------------------------------------

    /**
     * Estado simplificado del salto visto desde el handler.
     * PlayerAnimation gestiona los sub-estados de animación (JumpStart / Jumping
     * / JumpEnd) de forma autónoma; aquí solo necesitamos saber si estamos
     * en tierra o en el aire.
     */
    private enum JumpState {
        /** Personaje en suelo (o recién aterrizado, esperando a que JumpEnd termine). */
        GROUNDED,
        /** Personaje en el aire (JumpStart, Jumping). */
        AIRBORNE
    }

    // -----------------------------------------------------------------------
    // Campos
    // -----------------------------------------------------------------------

    private final CharacterControl control;
    private final PlayerAnimation  animation;

    /** Velocidad de traslación en unidades por segundo. */
    private float speed = 0.3f;

    /** Estado actual del salto. */
    private JumpState jumpState = JumpState.GROUNDED;

    /**
     * true si se solicitó un salto en este frame mediante el input.
     * Se consume en updateJumpAnimation() para evitar dobles activaciones.
     */
    private boolean jumpRequested = false;

    /**
     * Contador de frames consecutivos en tierra antes de considerar que el
     * personaje está realmente apoyado (filtra el "chattering" de onGround()).
     */
    private int groundFrames = 0;

    /** Frames consecutivos en tierra requeridos para considerar suelo estable. */
    private static final int REQUIRED_GROUND_FRAMES = 2;

    // -----------------------------------------------------------------------
    // Constructor
    // -----------------------------------------------------------------------

    public MovementHandler(CharacterControl control, PlayerAnimation animation) {
        this.control   = control;
        this.animation = animation;
    }

    // -----------------------------------------------------------------------
    // Actualización principal
    // -----------------------------------------------------------------------

    /**
     * Debe llamarse una vez por frame desde el AppState o AbstractControl
     * que gestione al jugador.
     *
     * @param input       Estado del teclado / gamepad del jugador.
     * @param tpf         Tiempo en segundos desde el último frame.
     * @param cameraYaw   Ángulo yaw de la cámara en radianes.
     * @param firstPerson true si la cámara está en modo primera persona.
     */
    public void update(InputPlayer input, float tpf, float cameraYaw, boolean firstPerson) {

        // ── 1. Calcular dirección de movimiento ─────────────────────────────
        Vector3f direction = computeMoveDirection(input, cameraYaw);
        boolean  isMoving  = direction.lengthSquared() > 0f;

        // ── 2. Calcular onGround con filtro de frames ────────────────────────
        boolean onGround = computeOnGround();

        // ── 3. Registrar solicitud de salto ──────────────────────────────────
        if (input.jump) {
            if (onGround) {
                jumpRequested = true;
                control.jump();     // Aplicar impulso físico inmediatamente.
            }
            input.jump = false;     // Consumir el evento de input en cualquier caso.
        }

        // ── 4. Actualizar máquina de estados de animación ────────────────────
        updateJumpAnimation(onGround, isMoving);

        // ── 5. Rotación del modelo ───────────────────────────────────────────
        animation.updateRotation(cameraYaw, direction, firstPerson);

        // ── 6. Aplicar dirección de movimiento a la física ───────────────────
        control.setWalkDirection(direction.multLocal(speed));
    }

    // -----------------------------------------------------------------------
    // Máquina de estados de salto
    // -----------------------------------------------------------------------

    /**
     * Coordina las llamadas a PlayerAnimation según el estado del salto.
     *
     * <pre>
     * GROUNDED ──(despegue)──► AIRBORNE
     *           ◄─(aterrizaje)─
     * </pre>
     *
     * Las transiciones internas JumpStart→Jumping y Jumping→JumpEnd son
     * responsabilidad exclusiva de PlayerAnimation (via Tweens).
     *
     * @param onGround true si el personaje está sobre suelo estable.
     * @param isMoving true si hay input de movimiento activo.
     */
    
    private void updateJumpAnimation(boolean onGround, boolean isMoving) {

        switch (jumpState) {

            case GROUNDED:
                if (!onGround) {
                    // ── Despegue detectado ──────────────────────────────────
                    if (jumpRequested) {
                        // Salto voluntario: JumpStart → Jumping (via PlayerAnimation).
                        animation.startJump();
                    } else {
                        // Caída inesperada (borde, pendiente): saltar directo a Jumping.
                        // startJump() inicia JumpStart incluso en caídas; si prefieres
                        // omitir JumpStart en caídas, llama directamente a un método
                        // específico o añade un parámetro booleano a startJump().
                        animation.startJump();
                    }
                    jumpState = JumpState.AIRBORNE;
                    jumpRequested = false;      // Consumido.

                } else {
                    // ── En tierra: locomoción normal ────────────────────────
                    // Solo cambiamos la animación base si no hay un salto activo
                    // (p. ej., mientras JumpEnd está terminando, isJumping() es true).
                    if (!animation.isJumping()) {
                        animation.setLocomotion(isMoving ? "Armature|Walk" : "Armature|Stand_Idle_0");
                    }
                }
                break;

            case AIRBORNE:
                if (onGround) {
                    // ── Aterrizaje detectado ────────────────────────────────
                    animation.landJump();       // Inicia JumpEnd; se limpia solo al terminar.
                    jumpState = JumpState.GROUNDED;
                    jumpRequested = false;      // Descartar solicitudes acumuladas en el aire.
                }
                // Si seguimos en el aire, PlayerAnimation mantiene Jumping en bucle;
                // no hacemos nada aquí.
                break;
        }
    }
    
    // -----------------------------------------------------------------------
    // Helpers privados
    // -----------------------------------------------------------------------

    /**
     * Calcula el vector de dirección de movimiento normalizado en función del
     * input y del yaw de cámara. Devuelve Vector3f.ZERO si no hay input.
     */
    private Vector3f computeMoveDirection(InputPlayer input, float cameraYaw) {
        float sinY = FastMath.sin(cameraYaw);
        float cosY = FastMath.cos(cameraYaw);

        Vector3f forward = new Vector3f( sinY, 0f, -cosY);
        Vector3f right   = new Vector3f( cosY, 0f,  sinY);
        Vector3f dir     = new Vector3f();

        if (input.forward)  dir.addLocal(forward);
        if (input.backward) dir.addLocal(forward.negate());
        if (input.right)    dir.addLocal(right);
        if (input.left)     dir.addLocal(right.negate());

        if (dir.lengthSquared() > 0f) dir.normalizeLocal();
        return dir;
    }

    /**
     * Devuelve true si el CharacterControl lleva al menos {@link #REQUIRED_GROUND_FRAMES}
     * frames consecutivos reportando onGround() == true.
     *
     * Esto filtra el "chattering" que ocurre cuando el motor físico oscila
     * entre contacto y no-contacto en superficies planas.
     */
    private boolean computeOnGround() {
        if (control.onGround()) {
            groundFrames++;
        } else {
            groundFrames = 0;
        }
        return groundFrames >= REQUIRED_GROUND_FRAMES;
    }

    // -----------------------------------------------------------------------
    // Getters / setters de configuración
    // -----------------------------------------------------------------------

    /** @return Velocidad de traslación actual (unidades/segundo). */
    public float getSpeed() { return speed; }

    /** @param speed Nueva velocidad de traslación (unidades/segundo). */
    public void setSpeed(float speed) { this.speed = speed; }
}