package mygame.states;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.bullet.BulletAppState;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import java.util.ArrayList;
import java.util.List;
import mygame.input.InputMappings;
import mygame.level.Level;
import mygame.surface.Surface;
import mygame.surface.types.BouncerPlatform;
import mygame.surface.types.FlatPlatform;
import mygame.surface.types.FunnelPlatform;
import mygame.surface.types.RampPlatform;

public class BuildState extends BaseAppState {

    private final PhaseListener listener;
    private Level level;
    private SimpleApplication app;
    private final BulletAppState physics;
    private final Node rootNode;

    private final List<Surface> placedPlatforms = new ArrayList<>();

    private int[] remaining;
    private int selectedType = 0;
    private static final int FLAT = 0, RAMP = 1, BOUNCER = 2, FUNNEL = 3;
    private static final int TYPE_COUNT = 4;

    private Surface ghostSurface;
    private boolean buildMode = false;

    public BuildState(PhaseListener listener, Level level,
                      BulletAppState physics, Node rootNode) {
        this.listener = listener;
        this.level = level;
        this.physics = physics;
        this.rootNode = rootNode;
    }

    @Override
    protected void initialize(Application app) {
        this.app = (SimpleApplication) app;
        resetInventory();
        createGhost();
    }

    public void setLevel(Level level) {
        this.level = level;
        resetInventory();
        recreateGhost();
    }

    public void clearPlatforms() {
        for (Surface s : placedPlatforms) s.detachFromWorld(rootNode, physics.getPhysicsSpace());
        placedPlatforms.clear();
    }

    private void resetInventory() {
        remaining = new int[]{
            level.getFlatCount(), level.getRampCount(),
            level.getBouncerCount(), level.getFunnelCount()
        };
        selectedType = firstAvailableType();
    }

    private int firstAvailableType() {
        for (int i = 0; i < TYPE_COUNT; i++) {
            if (remaining[i] > 0) return i;
        }
        return 0;
    }

    @Override
    public void update(float tpf) {
        if (!buildMode) return;
        updateGhost();
    }

    private void updateGhost() {
        CollisionResults results = new CollisionResults();
        Ray ray = new Ray(app.getCamera().getLocation(), app.getCamera().getDirection());
        rootNode.collideWith(ray, results);

        if (results.size() == 0) {
            ghostSurface.getNode().setCullHint(com.jme3.scene.Spatial.CullHint.Always);
            return;
        }

        CollisionResult hit = results.getClosestCollision();
        String hitName = hit.getGeometry().getName();
        if (hitName.startsWith("FlatGeom") || hitName.startsWith("RampGeom") ||
            hitName.startsWith("BouncerGeom") || hitName.startsWith("LeftGeom") ||
            hitName.startsWith("RightGeom")) {
            ghostSurface.getNode().setCullHint(com.jme3.scene.Spatial.CullHint.Always);
            return;
        }

        Vector3f hitPoint = hit.getContactPoint().add(hit.getContactNormal().mult(0.2f));
        ghostSurface.setPosition(hitPoint);
        ghostSurface.getNode().setCullHint(com.jme3.scene.Spatial.CullHint.Inherit);
    }

    public void onAction(String name, boolean isPressed) {
        if (!isPressed) return;
        switch (name) {
            case InputMappings.BUILD_TOGGLE -> toggleBuildMode();
            case InputMappings.CYCLE_TYPE   -> cycleType();
            case InputMappings.PLACE        -> { if (buildMode) placePlatform(); }
            case InputMappings.REMOVE       -> { if (buildMode) removePlatform(); }
            case InputMappings.RELEASE_BALL -> listener.onBuildComplete();
        }
    }

    public void onScroll(boolean up) {
        if (!buildMode) return;
        if (selectedType == RAMP && ghostSurface instanceof RampPlatform) {
            ((RampPlatform) ghostSurface).nextAngle();
        }
    }

    private void toggleBuildMode() {
        buildMode = !buildMode;
        if (!buildMode) {
            ghostSurface.getNode().setCullHint(com.jme3.scene.Spatial.CullHint.Always);
        }
    }

    private void cycleType() {
        for (int i = 1; i <= TYPE_COUNT; i++) {
            int next = (selectedType + i) % TYPE_COUNT;
            if (remaining[next] > 0) {
                selectedType = next;
                recreateGhost();
                return;
            }
        }
    }

    private void placePlatform() {
        if (remaining[selectedType] <= 0) return;

        CollisionResults results = new CollisionResults();
        Ray ray = new Ray(app.getCamera().getLocation(), app.getCamera().getDirection());
        rootNode.collideWith(ray, results);
        if (results.size() == 0) return;

        CollisionResult hit = results.getClosestCollision();
        String hitName = hit.getGeometry().getName();
        if (hitName.startsWith("FlatGeom") || hitName.startsWith("RampGeom") ||
            hitName.startsWith("BouncerGeom") || hitName.startsWith("LeftGeom") ||
            hitName.startsWith("RightGeom")) return;

        Vector3f pos = hit.getContactPoint().add(hit.getContactNormal().mult(0.2f));
        Surface s = createSurface(selectedType);
        s.setPosition(pos);
        if (selectedType == RAMP && ghostSurface instanceof RampPlatform) {
            s.setRotation(ghostSurface.getNode().getLocalRotation());
        }
        s.attachToWorld(rootNode, physics.getPhysicsSpace());
        placedPlatforms.add(s);
        remaining[selectedType]--;
    }

    private void removePlatform() {
        CollisionResults results = new CollisionResults();
        Ray ray = new Ray(app.getCamera().getLocation(), app.getCamera().getDirection());
        rootNode.collideWith(ray, results);
        if (results.size() == 0) return;

        CollisionResult hit = results.getClosestCollision();
        String hitName = hit.getGeometry().getName();
        int type = nameToType(hitName);
        if (type < 0) return;

        Node hitNode = hit.getGeometry().getParent();
        Surface toRemove = null;
        for (Surface s : placedPlatforms) {
            if (s.getNode() == hitNode || s.getNode() == hitNode.getParent()) {
                toRemove = s;
                break;
            }
        }
        if (toRemove != null) {
            toRemove.detachFromWorld(rootNode, physics.getPhysicsSpace());
            placedPlatforms.remove(toRemove);
            remaining[type]++;
        }
    }

    private int nameToType(String name) {
        if (name.startsWith("FlatGeom"))    return FLAT;
        if (name.startsWith("RampGeom"))    return RAMP;
        if (name.startsWith("BouncerGeom")) return BOUNCER;
        if (name.startsWith("LeftGeom") || name.startsWith("RightGeom")) return FUNNEL;
        return -1;
    }

    private void createGhost() {
        ghostSurface = createSurface(selectedType);
        ghostSurface.applyGhostMaterial(app.getAssetManager(),
                new ColorRGBA(0f, 1f, 0f, 0.4f));
        ghostSurface.getNode().setCullHint(com.jme3.scene.Spatial.CullHint.Always);
        rootNode.attachChild(ghostSurface.getNode());
    }

    private void recreateGhost() {
        if (ghostSurface != null) rootNode.detachChild(ghostSurface.getNode());
        createGhost();
    }

    private Surface createSurface(int type) {
        return switch (type) {
            case FLAT    -> new FlatPlatform(app);
            case RAMP    -> new RampPlatform(app);
            case BOUNCER -> new BouncerPlatform(app);
            default      -> new FunnelPlatform(app);
        };
    }

    public int[] getRemaining()  { return remaining; }
    public int getSelectedType() { return selectedType; }
    public boolean isBuildMode() { return buildMode; }

    @Override
    protected void cleanup(Application app) {
        if (ghostSurface != null) rootNode.detachChild(ghostSurface.getNode());
    }

    @Override protected void onEnable() {}
    @Override protected void onDisable() {}
}
