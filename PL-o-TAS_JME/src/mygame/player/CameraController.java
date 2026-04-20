/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mygame.player;

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
public class CameraController {
    private static final float SENSITIVITY  = 2.5f;
    private static final float PITCH_LIMIT  = FastMath.HALF_PI - 0.05f;
    private static final float EYE_HEIGHT   = 1.6f;

    private final Camera cam;
    private final Supplier<Vector3f> positionSource;

    private float yaw   = 0f;
    private float pitch = 0f;
    
    public CameraController(Application app, Supplier<Vector3f> positionSource) {
        this.cam            = ((SimpleApplication) app).getCamera();
        this.positionSource = positionSource;
    }
    
        // GameState llama esto cuando hay movimiento de raton
    public void updateMouse(float deltaX, float deltaY) {
        yaw   -= deltaX * SENSITIVITY;
        pitch += deltaY * SENSITIVITY;
        pitch  = FastMath.clamp(pitch, -PITCH_LIMIT, PITCH_LIMIT);
    }
    
        // GameState llama esto cada frame
    public void update(float tpf) {
        // Obtener posicion de referencia (puede ser player, un punto fijo, lo que sea)
        Vector3f eyePos = positionSource.get().add(0f, EYE_HEIGHT, 0f);
        cam.setLocation(eyePos);

        // Convertir yaw y pitch a un vector de direccion
        float cosP = FastMath.cos(pitch);
        Vector3f lookDir = new Vector3f(
             FastMath.sin(yaw) * cosP,
             FastMath.sin(pitch),
            -FastMath.cos(yaw) * cosP
        );

        cam.lookAtDirection(lookDir, Vector3f.UNIT_Y);
    }
    // Para que GameState pueda orientar el movimiento del player
    public float getYaw() { return yaw; }
    
}
