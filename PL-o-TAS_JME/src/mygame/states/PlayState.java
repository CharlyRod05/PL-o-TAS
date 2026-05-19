package mygame.states;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.control.GhostControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import java.util.HashSet;
import java.util.Set;
import mygame.ball.AbstractBall;
import mygame.ball.BallFactory;
import mygame.ball.BallType;
import mygame.level.Level;
import mygame.obstacle.Obstacle;
import mygame.ui.HUD;

public class PlayState extends BaseAppState implements PhysicsCollisionListener {

    private final PhaseListener listener;
    private final Level level;
    private final BulletAppState physics;
    private final Node rootNode;
    private final HUD hud;
    private SimpleApplication app;

    private AbstractBall activeBall;
    private boolean exploding = false;
    private boolean waitingRetry = false;
    private float explosionTimer = 0f;
    private static final float EXPLOSION_DURATION = 0.5f;
    private static final float FALL_Y = -20f;

    private final Set<RigidBodyControl> lethalBodies = new HashSet<>();

    public PlayState(PhaseListener listener, Level level,
                     BulletAppState physics, Node rootNode, HUD hud) {
        this.listener = listener;
        this.level = level;
        this.physics = physics;
        this.rootNode = rootNode;
        this.hud = hud;
    }

    @Override
    protected void initialize(Application app) {
        this.app = (SimpleApplication) app;

        for (Obstacle o : level.getObstacles()) {
            lethalBodies.addAll(o.getLethalBodies());
        }

        physics.getPhysicsSpace().addCollisionListener(this);

        BallFactory factory = new BallFactory(app.getAssetManager());
        activeBall = factory.create(BallType.BASIC);
        rootNode.attachChild(activeBall.getNode());
        activeBall.addToPhysicsSpace(physics.getPhysicsSpace());
        activeBall.setSpawnPosition(level.getSpawnPoint());
    }

    @Override
    public void update(float tpf) {
        if (activeBall == null) return;

        level.update(tpf);

        if (exploding) {
            explosionTimer += tpf;
            float scale = 1f + 3f * (explosionTimer / EXPLOSION_DURATION);
            activeBall.getNode().setLocalScale(scale);
            if (explosionTimer >= EXPLOSION_DURATION) {
                destroyBallAndNotify(PhaseResult.FAIL);
            }
            return;
        }

        Vector3f pos = activeBall.getPhysicsControl().getPhysicsLocation();
        if (pos.y < FALL_Y) {
            triggerExplosion();
            return;
        }

        for (Obstacle o : level.getObstacles()) {
            GhostControl ghost = o.getGhostControl();
            if (ghost != null) {
                for (PhysicsCollisionObject obj : ghost.getOverlappingObjects()) {
                    if (obj == activeBall.getPhysicsControl()) {
                        triggerExplosion();
                        return;
                    }
                }
            }
        }

        if (level.getGoal().overlaps(activeBall.getPhysicsControl())) {
            destroyBallAndNotify(PhaseResult.WIN);
        }
    }

    @Override
    public void collision(PhysicsCollisionEvent event) {
        if (exploding || activeBall == null) return;
        PhysicsCollisionObject a = event.getObjectA();
        PhysicsCollisionObject b = event.getObjectB();
        RigidBodyControl ballPhysics = activeBall.getPhysicsControl();
        if (a == ballPhysics || b == ballPhysics) {
            PhysicsCollisionObject other = (a == ballPhysics) ? b : a;
            if (lethalBodies.contains(other)) {
                triggerExplosion();
            }
        }
    }

    private void triggerExplosion() {
        if (exploding) return;
        exploding = true;
        explosionTimer = 0f;
        activeBall.getPhysicsControl().setLinearVelocity(Vector3f.ZERO);
        activeBall.getPhysicsControl().setAngularVelocity(Vector3f.ZERO);
    }

    private void destroyBallAndNotify(PhaseResult result) {
        if (activeBall != null) {
            activeBall.destroy(physics.getPhysicsSpace(), rootNode);
            activeBall = null;
        }
        if (result == PhaseResult.FAIL) hud.showRetryPrompt();
        listener.onPlayResult(result);
    }

    public void onRetry() {
        if (waitingRetry) {
            waitingRetry = false;
            listener.onPlayResult(PhaseResult.FAIL);
        }
    }

    @Override
    protected void cleanup(Application app) {
        physics.getPhysicsSpace().removeCollisionListener(this);
        if (activeBall != null) {
            activeBall.destroy(physics.getPhysicsSpace(), rootNode);
            activeBall = null;
        }
        lethalBodies.clear();
    }

    @Override protected void onEnable() {}
    @Override protected void onDisable() {}
}
