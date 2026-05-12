package mygame.player;
 
import com.jme3.anim.AnimComposer;
import com.jme3.anim.tween.Tweens;
import com.jme3.anim.tween.action.Action;
import com.jme3.anim.util.AnimMigrationUtils;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
 
/**
 * Gestiona las animaciones del jugador usando el sistema moderno de AnimComposer
 * de JMonkeyEngine 3.8.
 *
 * Capas utilizadas:
 *  - DEFAULT_LAYER : locomoción (Idle, Walk, Run, …)
 *  - JUMP_LAYER    : secuencia de salto (JumpStart → Jumping → JumpEnd)
 *
 * El sistema de capas permite que ambas animaciones coexistan, aunque en la
 * práctica el salto reemplaza visualmente a la locomoción al cubrir todo el cuerpo
 * con máscara null (si se desea separación por huesos, sustituir null por
 * ArmatureMask.createMask(skinningControl.getArmature(), "rootBone")).
 */
public class PlayerAnimation {
 
    /** Nombre de la capa de salto; debe ser único dentro del AnimComposer. */
    private static final String JUMP_LAYER = "Jump";
 
    private final Spatial model;
    private final AnimComposer composer;
 
    /**
     * Bandera de estado que indica si la capa de salto está activa.
     * Se pone en false cuando onJumpLanded() es invocado por el Tween.
     */
    private boolean jumpLayerActive = false;
 
    // -----------------------------------------------------------------------
    // Constructor
    // -----------------------------------------------------------------------
 
    public PlayerAnimation(Spatial model) {
        this.model = model;
        
        this.composer = ensureAnimComposer(model);
        for (String anim : composer.getAnimClipsNames()) {
            System.out.println(anim);
        }
        /*
         * Crea la capa de salto con máscara null (afecta a todos los huesos).
         * makeLayer() en JME 3.8 devuelve void; guardamos el nombre como
         * constante JUMP_LAYER para referirnos a ella en las llamadas al composer.
         */
        composer.makeLayer(JUMP_LAYER, null);
 
        // Estado inicial: Idle en la capa base.
        composer.setCurrentAction("Armature|Stand_Idle_0", AnimComposer.DEFAULT_LAYER);
    }
 
    // -----------------------------------------------------------------------
    // Locomoción
    // -----------------------------------------------------------------------
 
    /**
     * Cambia la animación de locomoción en la capa base (Idle, Walk, Run, …).
     * Todas las acciones de locomoción se reproducen en bucle por defecto.
     *
     * @param name Nombre exacto del clip de animación tal como aparece en el modelo.
     */
    public void setLocomotion(String name) {
        composer.setCurrentAction(name, AnimComposer.DEFAULT_LAYER);
    }
 
    // -----------------------------------------------------------------------
    // Salto — inicio
    // -----------------------------------------------------------------------
 
    /**
     * Inicia la secuencia de salto en la capa JUMP_LAYER:
     *   1. JumpStart  — se reproduce una sola vez (loop = false).
     *   2. Al terminar, Tweens.callMethod invoca playLoopingJump() que
     *      pone Jumping en bucle hasta que se llame a landJump().
     */
    public void startJump() {
        if (jumpLayerActive) {
            return; // Ya hay un salto en curso; ignorar.
        }
 
        /*
         * Construimos la secuencia:
         *   [ JumpStart (clip completo, sin bucle) ] → [ callback playLoopingJump ]
         *
         * actionSequence registra la acción bajo el nombre "JumpStartSeq" en el
         * mapa interno del composer; llamadas posteriores con el mismo nombre
         * simplemente sobreescriben la entrada, por lo que es seguro entre saltos.
         */
        composer.actionSequence(
                "JumpStartSeq",
                composer.action("Armature|Jump"),
                Tweens.callMethod(this, "playLoopingJump")
        );
 
        /*
         * setCurrentAction(name, layerName, loop):
         *   loop = false → la secuencia se reproduce una vez y el Tween al final
         *   dispara el callback. Sin el tercer argumento, la acción buclearía,
         *   lo que repetiría JumpStart indefinidamente.
         */
        composer.setCurrentAction("JumpStartSeq", JUMP_LAYER, false);
        jumpLayerActive = true;
    }
 
    /**
     * Callback invocado por Tweens.callMethod cuando JumpStart termina.
     * <p>
     * DEBE ser public: Tweens.callMethod usa reflexión para localizarlo.
     * No llamar directamente desde código externo.
     */
    public void playLoopingJump() {
        // Jumping se reproduce en bucle hasta que landJump() lo interrumpa.
        composer.setCurrentAction("Armature|Jump", JUMP_LAYER, true);
    }
 
    // -----------------------------------------------------------------------
    // Salto — aterrizaje
    // -----------------------------------------------------------------------
 
    /**
     * Inicia la fase de aterrizaje:
     *   1. JumpEnd — se reproduce una sola vez.
     *   2. Al terminar, Tweens.callMethod invoca onJumpLanded() que
     *      limpia la capa y restaura el estado.
     */
    public void landJump() {
        if (!jumpLayerActive) {
            return; // No hay salto activo; ignorar.
        }
 
        composer.actionSequence(
                "JumpEndSeq",
                composer.action("Armature|Jump"),
                Tweens.callMethod(this, "onJumpLanded")
        );
 
        composer.setCurrentAction("JumpEndSeq", JUMP_LAYER, false);
    }
 
    /**
     * Callback invocado por Tweens.callMethod cuando JumpEnd termina.
     * <p>
     * DEBE ser public: Tweens.callMethod usa reflexión para localizarlo.
     * No llamar directamente desde código externo.
     */
    public void onJumpLanded() {
        // Quita cualquier acción activa en la capa de salto.
        composer.removeCurrentAction(JUMP_LAYER);
        jumpLayerActive = false;
    }
 
    // -----------------------------------------------------------------------
    // Consultas de estado
    // -----------------------------------------------------------------------
 
    /**
     * @return true si la capa de salto está activa (JumpStart, Jumping o JumpEnd).
     */
    public boolean isJumping() {
        return jumpLayerActive;
    }
 
    // -----------------------------------------------------------------------
    // Limpieza
    // -----------------------------------------------------------------------
 
    /**
     * Libera recursos al destruir el jugador.
     * Ya no hay listeners que desregistrar; los Tweens son efímeros.
     */
    public void cleanup() {
        if (jumpLayerActive) {
            composer.removeCurrentAction(JUMP_LAYER);
            jumpLayerActive = false;
        }
    }
 
    // -----------------------------------------------------------------------
    // Rotación del modelo
    // -----------------------------------------------------------------------
 
    /**
     * Orienta el modelo según la cámara (primera persona) o la dirección de
     * movimiento (tercera persona).
     *
     * @param cameraYaw   Ángulo yaw actual de la cámara en radianes.
     * @param direction   Vector de movimiento (puede ser longitud cero = quieto).
     * @param firstPerson true si la cámara está en primera persona.
     */
    public void updateRotation(float cameraYaw, Vector3f direction, boolean firstPerson) {
        Quaternion rot = new Quaternion();
        if (firstPerson) {
            rot.fromAngleAxis(FastMath.PI - cameraYaw, Vector3f.UNIT_Y);
        } else {
            if (direction.lengthSquared() > 0f) {
                float angle = FastMath.atan2(-direction.x, direction.z) + FastMath.PI;
                rot.fromAngleAxis(-angle + FastMath.PI, Vector3f.UNIT_Y);
            }
        }
        model.setLocalRotation(rot);
    }
 
    // -----------------------------------------------------------------------
    // Utilidades estáticas
    // -----------------------------------------------------------------------
 
    /**
     * Garantiza que el modelo tenga un AnimComposer, migrando desde el sistema
     * antiguo si fuera necesario.
     *
     * @param model El Spatial del modelo del jugador.
     * @throws RuntimeException si la migración falla.
     */
    public static AnimComposer ensureAnimComposer(Spatial model) {

        AnimComposer composer
                = findAnimComposer(model);

        if (composer != null) {
            return composer;
        }

        // Migrar sistema viejo
        AnimMigrationUtils.migrate(model);

        composer = findAnimComposer(model);

        if (composer == null) {
            throw new RuntimeException(
                    "No se pudo obtener un AnimComposer."
            );
        }

        return composer;
    }
    private static AnimComposer findAnimComposer(Spatial spatial) {

        AnimComposer composer
                = spatial.getControl(AnimComposer.class);

        if (composer != null) {
            return composer;
        }

        if (spatial instanceof Node node) {

            for (Spatial child : node.getChildren()) {

                composer = findAnimComposer(child);

                if (composer != null) {
                    return composer;
                }
            }
        }

        return null;
    }
}