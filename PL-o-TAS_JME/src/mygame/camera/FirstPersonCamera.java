package mygame.camera;

import com.jme3.app.Application;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import java.util.function.Supplier;

/**
 *
 * @author rodri
 */
public class FirstPersonCamera extends CameraController{ 
    private static final float EYE_HEIGHT   = 1.6f;
    
    public FirstPersonCamera(Application app, Supplier<Vector3f> positionSource) {
        super(app,positionSource);
    }
        // GameState llama esto cuando hay movimiento de raton
    @Override
    public void updateMouse(float deltaX, float deltaY) {
        yaw   -= deltaX * SENSITIVITY;
        pitch += deltaY * SENSITIVITY;
        pitch  = FastMath.clamp(pitch, -PITCH_LIMIT, PITCH_LIMIT);
    }
    
        // GameState llama esto cada frame
    @Override
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
}
