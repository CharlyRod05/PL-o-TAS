/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mygame.states;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import mygame.input.AnalogReceiver;
import mygame.input.InputHandler;
import mygame.input.InputMappings;
import mygame.input.InputReceiver;
import mygame.player.CameraController;
import mygame.player.Player;
import mygame.world.World;



public class GameState extends BaseAppState implements InputReceiver,AnalogReceiver{
    private SimpleApplication app;
    World world;
    Player player;
    InputHandler inputHandler;
    CameraController cameraController;

    @Override
    protected void initialize(Application app) {
        this.app = (SimpleApplication) app;
        System.out.println("GameState inicializado");
        //crea world
        world = new World(app);
        this.app.getRootNode().attachChild(world.getNode());
        //creaPersonaje
        player = new Player(app,world.getPhysics());
        this.app.getRootNode().attachChild(player.getNode());
        //creaCamara
        cameraController = new CameraController(app, () -> player.getPosition());
        
        inputHandler = new InputHandler(app, this,this);
    }
    
    @Override
    public void update(float tpf) {
        // La camara primero para que el yaw este actualizado cuando el player lo use
        cameraController.update(tpf);
        player.setCameraYaw(cameraController.getYaw());
        player.update();
    }
    
    @Override
    public void onAction(String name, boolean isPressed) {
        if (name.equals(InputMappings.P_JUMP) && isPressed) {
            player.jump();
        }
        if (name.equals(InputMappings.MOVE_FORWARD)) {
            player.setForward(isPressed);
        }
        if (name.equals(InputMappings.MOVE_BACKWARD)) {
            player.setBackward(isPressed);
        }
        if (name.equals(InputMappings.MOVE_LEFT)) {
            player.setLeft(isPressed);
        }
        if (name.equals(InputMappings.MOVE_RIGHT)) {
            player.setRight(isPressed);
        }
    }
    
    @Override
    public void onAnalog(String name, float value) {
        switch (name) {
            case InputMappings.CAM_MOUSE_LEFT  -> cameraController.updateMouse( value, 0);
            case InputMappings.CAM_MOUSE_RIGHT -> cameraController.updateMouse(-value, 0);
            case InputMappings.CAM_MOUSE_UP    -> cameraController.updateMouse(0,  value);
            case InputMappings.CAM_MOUSE_DOWN  -> cameraController.updateMouse(0, -value);
        }

    }

    @Override
    protected void cleanup(Application app) {
        inputHandler.cleanup();
    }

    @Override
    protected void onEnable() {
    }

    @Override
    protected void onDisable() {
    }

    

    
}
