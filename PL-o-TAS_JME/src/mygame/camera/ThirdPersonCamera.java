package mygame.camera;

import com.jme3.app.Application;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import java.util.function.Supplier;


/**
 *
 * @author rodri
 */
public class ThirdPersonCamera extends CameraController {
    private final float distance = 5f;
    private final float height   = 2f;
    
    
    public ThirdPersonCamera(Application app, Supplier<Vector3f> positionSource) {
        super(app, positionSource);
    }
    
    @Override
    public void updateMouse(float deltaX, float deltaY) {
        yaw -= deltaX * SENSITIVITY;
        pitch -= deltaY * SENSITIVITY;
        pitch = FastMath.clamp(pitch, -FastMath.QUARTER_PI , PITCH_LIMIT);
    }
    
    @Override
    public void update(float tpf) {
        Vector3f target = positionSource.get();

        // ← Coordenadas esféricas: yaw da la vuelta horizontal,
        //   pitch inclina arriba/abajo el brazo de la cámara
        float sinP = FastMath.sin(pitch);
        float cosP = FastMath.cos(pitch);
        float sinY = FastMath.sin(yaw);
        float cosY = FastMath.cos(yaw);

        Vector3f camPos = target.add(
                -sinY * distance * cosP, // X
                sinP * distance + height, // Y
                cosY * distance * cosP // Z
        );

        cam.setLocation(camPos);
        cam.lookAt(target.add(0, 1, 0), Vector3f.UNIT_Y);
    }
    
    @Override
    public void setOrientation(float yaw, float pitch) {
        this.yaw = yaw;
        this.pitch = FastMath.clamp(pitch, -FastMath.QUARTER_PI, PITCH_LIMIT);
    }
    
    
    
}
