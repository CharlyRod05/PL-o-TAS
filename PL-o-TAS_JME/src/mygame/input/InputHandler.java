/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mygame.input;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;

/**
 *
 * @author rodri
 */
public class InputHandler {
    
    SimpleApplication app;
    InputReceiver iRec;
    AnalogReceiver anRec;
    
    
    public InputHandler(Application app, InputReceiver iRec, AnalogReceiver anRec) {
        this.app = (SimpleApplication) app;
        this.iRec = iRec;
        this.anRec = anRec;

        initInput();
    }
    
    private void initInput(){
        app.getFlyByCamera().setEnabled(false);          // ← apagar cámara nativa
        app.getInputManager().setCursorVisible(false);   // ← ocultar cursor
        
        
        //Inputs player
        app.getInputManager().addMapping(InputMappings.MOVE_FORWARD, new KeyTrigger(KeyInput.KEY_W));
        app.getInputManager().addMapping(InputMappings.MOVE_BACKWARD, new KeyTrigger(KeyInput.KEY_S));
        app.getInputManager().addMapping(InputMappings.MOVE_LEFT, new KeyTrigger(KeyInput.KEY_A));
        app.getInputManager().addMapping(InputMappings.MOVE_RIGHT, new KeyTrigger(KeyInput.KEY_D));
        app.getInputManager().addMapping(InputMappings.P_JUMP, new KeyTrigger(KeyInput.KEY_SPACE));
        
        //Input switch camara
        app.getInputManager().addMapping(InputMappings.CAM_SWITCH, new KeyTrigger(KeyInput.KEY_H));
        app.getInputManager().addListener(actionListener,
                InputMappings.MOVE_FORWARD, InputMappings.MOVE_BACKWARD, InputMappings.MOVE_LEFT, InputMappings.MOVE_RIGHT, InputMappings.P_JUMP,InputMappings.CAM_SWITCH);
        
        
        //Inputs camara
        app.getInputManager().addMapping(InputMappings.CAM_MOUSE_UP, new MouseAxisTrigger(MouseInput.AXIS_Y, false));
        app.getInputManager().addMapping(InputMappings.CAM_MOUSE_DOWN, new MouseAxisTrigger(MouseInput.AXIS_Y, true));
        app.getInputManager().addMapping(InputMappings.CAM_MOUSE_LEFT, new MouseAxisTrigger(MouseInput.AXIS_X, true));
        app.getInputManager().addMapping(InputMappings.CAM_MOUSE_RIGHT, new MouseAxisTrigger(MouseInput.AXIS_X, false));
        

        app.getInputManager().addListener(analogListener,
                InputMappings.CAM_MOUSE_UP,InputMappings.CAM_MOUSE_DOWN,InputMappings.CAM_MOUSE_LEFT,InputMappings.CAM_MOUSE_RIGHT);

    }
    
    private final ActionListener actionListener = new ActionListener() {
            @Override
            public void onAction(String name, boolean isPressed, float tpf){
                iRec.onAction(name,isPressed);
            }
    };
    
    private final AnalogListener analogListener = new AnalogListener() {
            @Override
            public void onAnalog(String name, float value, float tpf) {
                anRec.onAnalog(name, value);
            }
    };
    
    public void cleanup() {
        app.getInputManager().removeListener(actionListener);
        app.getInputManager().removeListener(analogListener);

        app.getInputManager().deleteMapping(InputMappings.MOVE_FORWARD);
        app.getInputManager().deleteMapping(InputMappings.MOVE_BACKWARD);
        app.getInputManager().deleteMapping(InputMappings.MOVE_LEFT);
        app.getInputManager().deleteMapping(InputMappings.MOVE_RIGHT);
        app.getInputManager().deleteMapping(InputMappings.P_JUMP);
        app.getInputManager().deleteMapping(InputMappings.CAM_MOUSE_UP);
        app.getInputManager().deleteMapping(InputMappings.CAM_MOUSE_DOWN);
        app.getInputManager().deleteMapping(InputMappings.CAM_MOUSE_LEFT);
        app.getInputManager().deleteMapping(InputMappings.CAM_MOUSE_RIGHT);
        app.getInputManager().deleteMapping(InputMappings.CAM_SWITCH);
        
        app.getFlyByCamera().setEnabled(true);
        app.getInputManager().setCursorVisible(true);
    }
}
