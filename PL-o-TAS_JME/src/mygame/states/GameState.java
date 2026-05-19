package mygame.states;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.bullet.BulletAppState;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import mygame.camera.CameraController;
import mygame.camera.FirstPersonCamera;
import mygame.camera.ThirdPersonCamera;
import mygame.input.AnalogReceiver;
import mygame.input.InputHandler;
import mygame.input.InputMappings;
import mygame.input.InputReceiver;
import mygame.level.Level;
import mygame.level.levels.Level1;
import mygame.level.levels.Level2;
import mygame.level.levels.Level3;
import mygame.player.BuildController;
import mygame.player.InputPlayer;
import mygame.player.Player;
import mygame.player.PlayerController;
import mygame.player.WalkController;
import mygame.ui.HUD;
import mygame.world.World;

public class GameState extends BaseAppState
        implements InputReceiver, AnalogReceiver, PhaseListener {

    private SimpleApplication app;
    private World world;
    private Player player;
    private InputHandler inputHandler;
    private boolean firstPerson = true;
    private CameraController cameraController;
    private FirstPersonCamera camF;
    private ThirdPersonCamera camT;
    private BulletAppState physics;
    private InputPlayer inputPlayer;
    private PlayerController currentController;

    private Level currentLevel;
    private int levelIndex = 0;

    private BuildState buildState;
    private PlayState playState;
    private HUD hud;

    @Override
    protected void initialize(Application app) {
        this.app = (SimpleApplication) app;

        world = new World(app);
        this.app.getRootNode().attachChild(world.getNode());
        physics = world.getPhysics();

        player = new Player(app, physics);
        this.app.getRootNode().attachChild(player.getNode());

        camT = new ThirdPersonCamera(app, () -> player.getPosition());
        camF = new FirstPersonCamera(app, () -> player.getPosition());
        cameraController = camF;

        currentController = new WalkController(player);
        inputPlayer = new InputPlayer();
        inputHandler = new InputHandler(app, this, this);

        AmbientLight ambient = new AmbientLight();
        ambient.setColor(ColorRGBA.White.mult(0.4f));
        this.app.getRootNode().addLight(ambient);
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-0.5f, -1f, -0.5f).normalizeLocal());
        sun.setColor(ColorRGBA.White.mult(0.8f));
        this.app.getRootNode().addLight(sun);

        hud = new HUD(app);

        loadLevel(0);
    }

    private Level createLevel(int index) {
        Level l = switch (index % 3) {
            case 0 -> new Level1();
            case 1 -> new Level2();
            default -> new Level3();
        };
        l.setup(app);
        return l;
    }

    private void loadLevel(int index) {
        levelIndex = index;
        currentLevel = createLevel(index);
        currentLevel.attachToWorld(this.app.getRootNode(), physics.getPhysicsSpace());

        if (buildState == null) {
            buildState = new BuildState(this, currentLevel,
                    physics, this.app.getRootNode());
            getStateManager().attach(buildState);
        } else {
            buildState.setLevel(currentLevel);
            buildState.clearPlatforms();
            if (!buildState.isEnabled()) getStateManager().attach(buildState);
        }

        currentController = new BuildController(player);
        hud.showBuildHUD(currentLevel, buildState.getRemaining(), buildState.isBuildMode());
    }

    @Override
    public void update(float tpf) {
        cameraController.update(tpf);
        float yaw = cameraController.getYaw();
        player.setFirstPerson(firstPerson);
        currentController.update(inputPlayer, tpf, yaw);
        inputPlayer.reset();

        if (buildState != null && buildState.isEnabled()) {
            hud.updateBuildCounter(buildState.getRemaining(), buildState.isBuildMode());
        }
    }

    @Override
    public void onAction(String name, boolean isPressed) {
        if (name.equals(InputMappings.P_JUMP) && isPressed)      inputPlayer.jump = true;
        if (name.equals(InputMappings.MOVE_FORWARD))             inputPlayer.forward = isPressed;
        if (name.equals(InputMappings.MOVE_BACKWARD))            inputPlayer.backward = isPressed;
        if (name.equals(InputMappings.MOVE_LEFT))                inputPlayer.left = isPressed;
        if (name.equals(InputMappings.MOVE_RIGHT))               inputPlayer.right = isPressed;
        if (name.equals(InputMappings.CAM_SWITCH) && isPressed)  switchCamera();
        if (buildState != null && buildState.isEnabled()) {
            buildState.onAction(name, isPressed);
        }
        if (name.equals(InputMappings.RETRY) && isPressed) {
            if (playState != null) playState.onRetry();
        }
    }

    @Override
    public void onAnalog(String name, float value) {
        switch (name) {
            case InputMappings.CAM_MOUSE_LEFT  -> cameraController.updateMouse( value, 0);
            case InputMappings.CAM_MOUSE_RIGHT -> cameraController.updateMouse(-value, 0);
            case InputMappings.CAM_MOUSE_UP    -> cameraController.updateMouse(0,  value);
            case InputMappings.CAM_MOUSE_DOWN  -> cameraController.updateMouse(0, -value);
            case InputMappings.SCROLL_UP   -> { if (buildState != null) buildState.onScroll(true);  }
            case InputMappings.SCROLL_DOWN -> { if (buildState != null) buildState.onScroll(false); }
        }
    }

    @Override
    public void onBuildComplete() {
        if (buildState != null) getStateManager().detach(buildState);
        playState = new PlayState(this, currentLevel, physics, app.getRootNode(), hud);
        getStateManager().attach(playState);
        currentController = new WalkController(player);
        hud.showPlayHUD();
    }

    @Override
    public void onPlayResult(PhaseResult result) {
        if (playState != null) {
            getStateManager().detach(playState);
            playState = null;
        }

        if (result == PhaseResult.WIN) {
            int nextIndex = levelIndex + 1;
            if (nextIndex >= 3) {
                hud.showCompletedMessage();
                currentLevel.detachFromWorld(app.getRootNode(), physics.getPhysicsSpace());
                loadLevel(0);
            } else {
                currentLevel.detachFromWorld(app.getRootNode(), physics.getPhysicsSpace());
                loadLevel(nextIndex);
            }
        } else {
            currentController = new BuildController(player);
            getStateManager().attach(buildState);
            hud.showBuildHUD(currentLevel, buildState.getRemaining(), buildState.isBuildMode());
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

    @Override
    protected void cleanup(Application app) {
        inputHandler.cleanup();
    }

    @Override protected void onEnable() {}
    @Override protected void onDisable() {}
}
