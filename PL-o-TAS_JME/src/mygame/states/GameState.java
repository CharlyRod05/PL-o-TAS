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
import com.jme3.math.FastMath;
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
import mygame.player.BuildController;
import mygame.player.InputPlayer;
import mygame.player.Player;
import mygame.player.PlayerController;
import mygame.player.WalkController;
import mygame.world.World;



public class GameState extends BaseAppState implements InputReceiver,AnalogReceiver{
    private SimpleApplication app;
    private World world;
    private Player player;
    private InputHandler inputHandler;
    private boolean firstPerson = true;
    private CameraController cameraController;
    private FirstPersonCamera camF;
    private ThirdPersonCamera camT;
    private BulletAppState physics;
    private BallSource ballSource;

    // ECS ligero
    private InputPlayer inputPlayer;
    private PlayerController currentController;

    @Override
    protected void initialize(Application app) {
        this.app = (SimpleApplication) app;

        // Mundo
        world = new World(app);
        this.app.getRootNode().attachChild(world.getNode());
        physics = world.getPhysics();

        // Jugador
        player = new Player(app, physics);
        this.app.getRootNode().attachChild(player.getNode());

        // Cámaras
        camT = new ThirdPersonCamera(app, () -> player.getPosition());
        camF = new FirstPersonCamera(app, () -> player.getPosition());
        cameraController = camF;

        // Controlador inicial (modo exploración)
        currentController = new WalkController(player);

        // Snapshot de inputs
        inputPlayer = new InputPlayer();

        // Manejador de inputs
        inputHandler = new InputHandler(app, this, this);

        // Luces
        AmbientLight ambient = new AmbientLight();
        ambient.setColor(ColorRGBA.White.mult(0.4f));
        this.app.getRootNode().addLight(ambient);
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-0.5f, -1f, -0.5f).normalizeLocal());
        sun.setColor(ColorRGBA.White.mult(0.8f));
        this.app.getRootNode().addLight(sun);

        // Pelotas
        initBallSource();
    }

    private void initBallSource() {
        BallFactory factory = new BallFactory(app.getAssetManager());
        ballSource = new BallSource("MainSource", factory, this.app.getRootNode(), physics.getPhysicsSpace(), BallType.BASIC);
        ballSource.getNode().setLocalTranslation(0f, 8f, 0f);
        ballSource.setMaxBalls(50);
        ballSource.setAutoSpawn(true, 0.2f);
        this.app.getRootNode().attachChild(ballSource.getNode());
    }

    @Override
    public void update(float tpf) {

        // Actualizar cámara y obtener orientación
        cameraController.update(tpf);
        float yaw = cameraController.getYaw();
        player.setFirstPerson(firstPerson);

        // El controlador activo procesa los inputs y mueve/actualiza al jugador
        currentController.update(inputPlayer, tpf, yaw);

        // Pelotas
        ballSource.update(tpf);
    }

    // --- InputReceiver ---
    @Override
    public void onAction(String name, boolean isPressed) {
        if (name.equals(InputMappings.P_JUMP) && isPressed)       inputPlayer.jump = true;
        if (name.equals(InputMappings.MOVE_FORWARD))              inputPlayer.forward = isPressed;
        if (name.equals(InputMappings.MOVE_BACKWARD))             inputPlayer.backward = isPressed;
        if (name.equals(InputMappings.MOVE_LEFT))                 inputPlayer.left = isPressed;
        if (name.equals(InputMappings.MOVE_RIGHT))                inputPlayer.right = isPressed;
        if (name.equals(InputMappings.CAM_SWITCH) && isPressed)   switchCamera();
        //if (name.equals(InputMappings.BUILD_MODE) && isPressed)   toggleBuildMode();
        // Acciones de construcción (ratón, etc.) se asignarán más tarde a placeBlock/breakBlock
    }

    // --- AnalogReceiver ---
    @Override
    public void onAnalog(String name, float value) {
        switch (name) {
            case InputMappings.CAM_MOUSE_LEFT  -> cameraController.updateMouse( value, 0);
            case InputMappings.CAM_MOUSE_RIGHT -> cameraController.updateMouse(-value, 0);
            case InputMappings.CAM_MOUSE_UP    -> cameraController.updateMouse(0,  value);
            case InputMappings.CAM_MOUSE_DOWN  -> cameraController.updateMouse(0, -value);
        }
    }

    private void switchCamera() {
        if (firstPerson) {
            firstPerson = false;
            camT.setOrientation(camF.getYaw(), -camF.getPitch());
            cameraController = camT;
        } else {
            firstPerson = true;
            camF.setOrientation(camT.getYaw(), -camT.getPitch());
            cameraController = camF;
        }
    }

    private void toggleBuildMode() {
        if (currentController instanceof WalkController) {
            currentController = new BuildController(player);
            System.out.println("Modo construcción activado");
        } else {
            currentController = new WalkController(player);
            System.out.println("Modo exploración activado");
        }
    }

    @Override
    protected void cleanup(Application app) {
        inputHandler.cleanup();
    }

    @Override protected void onEnable() {}
    @Override protected void onDisable() {}
}

