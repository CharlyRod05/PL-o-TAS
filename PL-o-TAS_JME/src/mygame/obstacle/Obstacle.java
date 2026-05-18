package mygame.obstacle;

import com.jme3.app.Application;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.control.GhostControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public abstract class Obstacle {
    protected Node obstacleNode;
    protected final Application app;
    protected final Set<RigidBodyControl> lethalBodies = new HashSet<>();

    public Obstacle(String name, Application app) {
        this.app = app;
        this.obstacleNode = new Node(name);
        buildGeometry();
        buildPhysics();
    }

    protected abstract void buildGeometry();
    protected abstract void buildPhysics();

    public void attachToWorld(Node worldNode, PhysicsSpace space) {
        worldNode.attachChild(obstacleNode);
        for (RigidBodyControl rbc : lethalBodies) space.add(rbc);
        GhostControl ghost = getGhostControl();
        if (ghost != null) space.add(ghost);
    }

    public void detachFromWorld(Node worldNode, PhysicsSpace space) {
        worldNode.detachChild(obstacleNode);
        for (RigidBodyControl rbc : lethalBodies) space.remove(rbc);
        GhostControl ghost = getGhostControl();
        if (ghost != null) space.remove(ghost);
    }

    public void setPosition(Vector3f position) {
        obstacleNode.setLocalTranslation(position);
    }

    public void update(float tpf) {}

    public Set<RigidBodyControl> getLethalBodies() {
        return Collections.unmodifiableSet(lethalBodies);
    }

    public GhostControl getGhostControl() {
        return null;
    }

    public Node getNode() { return obstacleNode; }
}
