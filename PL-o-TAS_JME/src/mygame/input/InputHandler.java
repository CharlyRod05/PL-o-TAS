package mygame.input;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;

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

    private void initInput() {
        app.getFlyByCamera().setEnabled(false);
        app.getInputManager().setCursorVisible(false);

        app.getInputManager().addMapping(InputMappings.MOVE_FORWARD,  new KeyTrigger(KeyInput.KEY_W));
        app.getInputManager().addMapping(InputMappings.MOVE_BACKWARD, new KeyTrigger(KeyInput.KEY_S));
        app.getInputManager().addMapping(InputMappings.MOVE_LEFT,     new KeyTrigger(KeyInput.KEY_A));
        app.getInputManager().addMapping(InputMappings.MOVE_RIGHT,    new KeyTrigger(KeyInput.KEY_D));
        app.getInputManager().addMapping(InputMappings.P_JUMP,        new KeyTrigger(KeyInput.KEY_SPACE));
        app.getInputManager().addMapping(InputMappings.CAM_SWITCH,    new KeyTrigger(KeyInput.KEY_H));
        app.getInputManager().addMapping(InputMappings.BUILD_TOGGLE,  new KeyTrigger(KeyInput.KEY_B));
        app.getInputManager().addMapping(InputMappings.CYCLE_TYPE,    new KeyTrigger(KeyInput.KEY_TAB));
        app.getInputManager().addMapping(InputMappings.RELEASE_BALL,  new KeyTrigger(KeyInput.KEY_RETURN));
        app.getInputManager().addMapping(InputMappings.RETRY,         new KeyTrigger(KeyInput.KEY_R));
        app.getInputManager().addMapping(InputMappings.PLACE,  new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        app.getInputManager().addMapping(InputMappings.REMOVE, new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));

        app.getInputManager().addListener(actionListener,
                InputMappings.MOVE_FORWARD, InputMappings.MOVE_BACKWARD,
                InputMappings.MOVE_LEFT, InputMappings.MOVE_RIGHT,
                InputMappings.P_JUMP, InputMappings.CAM_SWITCH,
                InputMappings.BUILD_TOGGLE, InputMappings.CYCLE_TYPE,
                InputMappings.RELEASE_BALL, InputMappings.RETRY,
                InputMappings.PLACE, InputMappings.REMOVE);

        app.getInputManager().addMapping(InputMappings.CAM_MOUSE_UP,    new MouseAxisTrigger(MouseInput.AXIS_Y, false));
        app.getInputManager().addMapping(InputMappings.CAM_MOUSE_DOWN,  new MouseAxisTrigger(MouseInput.AXIS_Y, true));
        app.getInputManager().addMapping(InputMappings.CAM_MOUSE_LEFT,  new MouseAxisTrigger(MouseInput.AXIS_X, true));
        app.getInputManager().addMapping(InputMappings.CAM_MOUSE_RIGHT, new MouseAxisTrigger(MouseInput.AXIS_X, false));
        app.getInputManager().addMapping(InputMappings.SCROLL_UP,   new MouseAxisTrigger(MouseInput.AXIS_WHEEL, false));
        app.getInputManager().addMapping(InputMappings.SCROLL_DOWN,  new MouseAxisTrigger(MouseInput.AXIS_WHEEL, true));

        app.getInputManager().addListener(analogListener,
                InputMappings.CAM_MOUSE_UP, InputMappings.CAM_MOUSE_DOWN,
                InputMappings.CAM_MOUSE_LEFT, InputMappings.CAM_MOUSE_RIGHT,
                InputMappings.SCROLL_UP, InputMappings.SCROLL_DOWN);
    }

    private final ActionListener actionListener = (name, isPressed, tpf) -> iRec.onAction(name, isPressed);
    private final AnalogListener analogListener = (name, value, tpf) -> anRec.onAnalog(name, value);

    public void cleanup() {
        app.getInputManager().removeListener(actionListener);
        app.getInputManager().removeListener(analogListener);

        for (String m : new String[]{
                InputMappings.MOVE_FORWARD, InputMappings.MOVE_BACKWARD,
                InputMappings.MOVE_LEFT, InputMappings.MOVE_RIGHT,
                InputMappings.P_JUMP, InputMappings.CAM_SWITCH,
                InputMappings.BUILD_TOGGLE, InputMappings.CYCLE_TYPE,
                InputMappings.RELEASE_BALL, InputMappings.RETRY,
                InputMappings.PLACE, InputMappings.REMOVE,
                InputMappings.CAM_MOUSE_UP, InputMappings.CAM_MOUSE_DOWN,
                InputMappings.CAM_MOUSE_LEFT, InputMappings.CAM_MOUSE_RIGHT,
                InputMappings.SCROLL_UP, InputMappings.SCROLL_DOWN}) {
            app.getInputManager().deleteMapping(m);
        }
        app.getFlyByCamera().setEnabled(true);
        app.getInputManager().setCursorVisible(true);
    }
}
