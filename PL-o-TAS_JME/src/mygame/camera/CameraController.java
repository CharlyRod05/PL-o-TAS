/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mygame.camera;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import java.util.function.Supplier;

/**
 *
 * @author rodri
 */
public abstract class CameraController {
    // Atributos comunes a todas las camaras

    protected final Camera cam;
    protected final Supplier<Vector3f> positionSource;
    protected float yaw = 0f;
    protected float pitch = 0f;

    // Constantes comunes
    protected static final float SENSITIVITY = 2.5f;
    protected static final float PITCH_LIMIT = FastMath.HALF_PI - 0.05f;

    // Constructor comun, ninguna subclase necesita repetir esto
    public CameraController(Application app, Supplier<Vector3f> positionSource) {
        this.cam = ((SimpleApplication) app).getCamera();
        this.positionSource = positionSource;
    }

    // Obligatorio — cada camara decide como posicionarse y orientarse
    public abstract void update(float tpf);

    // Opcional — solo las camaras que usan raton lo sobreescriben
    public void updateMouse(float deltaX, float deltaY) {
    }

    // Comun a todas — GameState lo usa para orientar el movimiento del player
    public float getYaw() {
        return yaw;
    }
}
