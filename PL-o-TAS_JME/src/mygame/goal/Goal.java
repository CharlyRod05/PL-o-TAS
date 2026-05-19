package mygame.goal;

import com.jme3.app.Application;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.collision.shapes.CylinderCollisionShape;
import com.jme3.bullet.control.GhostControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Cylinder;

public class Goal {
    private final Node goalNode = new Node("Goal");
    private GhostControl ghostControl;
    private static final float RADIUS = 1.5f;
    private static final float HEIGHT = 1.0f;

    public Goal(Application app) {
        Cylinder mesh = new Cylinder(16, 16, RADIUS, HEIGHT, true);
        Geometry geom = new Geometry("GoalGeom", mesh);
        Material mat = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", new ColorRGBA(0f, 1f, 0.3f, 0.5f));
        mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        geom.setMaterial(mat);
        geom.setQueueBucket(RenderQueue.Bucket.Transparent);
        goalNode.attachChild(geom);

        ghostControl = new GhostControl(new CylinderCollisionShape(
                new Vector3f(RADIUS, HEIGHT / 2f, RADIUS), 1));
        goalNode.addControl(ghostControl);
    }

    public void setPosition(Vector3f position) {
        goalNode.setLocalTranslation(position);
    }

    public void attachToWorld(Node worldNode, PhysicsSpace space) {
        worldNode.attachChild(goalNode);
        space.add(ghostControl);
    }

    public void detachFromWorld(Node worldNode, PhysicsSpace space) {
        worldNode.detachChild(goalNode);
        space.remove(ghostControl);
    }

    public boolean overlaps(RigidBodyControl ballControl) {
        for (PhysicsCollisionObject obj : ghostControl.getOverlappingObjects()) {
            if (obj == ballControl) return true;
        }
        return false;
    }

    public Node getNode() { return goalNode; }
}
