/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
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
        pitch += deltaY * SENSITIVITY;
        pitch = FastMath.clamp(pitch, -PITCH_LIMIT, PITCH_LIMIT);
    }
    
    @Override
    public void update(float tpf) {
        Vector3f target = positionSource.get();
        // Obtener posicion de referencia (puede ser player, un punto fijo, lo que sea)
        

        // Convertir yaw y pitch a un vector de direccion
        Vector3f camPos = target.add(
                -FastMath.sin(yaw) * distance, // X — detras en horizontal
                height, // Y — un poco arriba
                FastMath.cos(yaw) * distance // Z — detras en profundidad
        );
        cam.setLocation(camPos);

        cam.lookAt(target.add(0,1,0),Vector3f.UNIT_Y);    
    }
    
    
    
}
