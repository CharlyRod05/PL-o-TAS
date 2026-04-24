/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mygame.states;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.bullet.BulletAppState;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import mygame.ball.BallFactory;
import mygame.ball.BallSource;
import mygame.ball.BallType;
import mygame.camera.CameraController;
import mygame.input.AnalogReceiver;
import mygame.input.InputHandler;
import mygame.input.InputMappings;
import mygame.input.InputReceiver;
import mygame.camera.FirstPersonCamera;
import mygame.camera.ThirdPersonCamera;
import mygame.player.Player;
import mygame.world.World;



public class GameState extends BaseAppState implements InputReceiver,AnalogReceiver{
    private SimpleApplication app;
    World world;
    Player player;
    InputHandler inputHandler;
    boolean firstPerson = true;
    CameraController cameraController;
    FirstPersonCamera camF;
    ThirdPersonCamera camT;
    BulletAppState physics;
    BallSource ballSource;
    

    @Override
    protected void initialize(Application app) {
        this.app = (SimpleApplication) app;
        System.out.println("GameState inicializado");
        //crea world
        world = new World(app);
        this.app.getRootNode().attachChild(world.getNode());
        //creaPersonaje
        physics = world.getPhysics();
        player = new Player(app,physics);
        this.app.getRootNode().attachChild(player.getNode());
        //creaCamara
        camT = new ThirdPersonCamera(app, () -> player.getPosition());
        camF = new FirstPersonCamera(app, () -> player.getPosition());
        
        cameraController = camF;
        //crear ballsource
        initBallSource();
        inputHandler = new InputHandler(app, this,this);
        
        
        // Luz ambiental — ilumina todo parejo
        AmbientLight ambient = new AmbientLight();
        ambient.setColor(ColorRGBA.White.mult(0.4f));
        this.app.getRootNode().addLight(ambient);

        // Luz direccional — simula el sol, da sombras
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-0.5f, -1f, -0.5f).normalizeLocal());
        sun.setColor(ColorRGBA.White.mult(0.8f));
        this.app.getRootNode().addLight(sun);
        
    }
    
    private void initBallSource() {
        BallFactory factory = new BallFactory(app.getAssetManager());

        ballSource = new BallSource(
                "MainSource",
                factory,
                this.app.getRootNode(),
                physics.getPhysicsSpace(),
                BallType.BASIC
        );

        ballSource.getNode().setLocalTranslation(0f, 8f, 0f);
        ballSource.setMaxBalls(50);
        ballSource.setAutoSpawn(true, 0.2f);

        this.app.getRootNode().attachChild(ballSource.getNode());
    }
    
    @Override
    public void update(float tpf) {
        // La camara primero para que el yaw este actualizado cuando el player lo use
        cameraController.update(tpf);
        player.setCameraYaw(cameraController.getYaw());
        player.update();
        ballSource.update(tpf);
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
        if ((name.equals(InputMappings.CAM_SWITCH)&& isPressed)){
            switchCamera();
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

    private void switchCamera(){
        if(firstPerson){
            firstPerson=false;
            cameraController = camT;
        }else{
            firstPerson=true;
            cameraController = camF;
        }
    }

    
}
