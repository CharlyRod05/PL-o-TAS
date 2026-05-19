package mygame.surface;

import com.jme3.app.Application;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.SceneGraphVisitor;
import com.jme3.scene.Spatial;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import mygame.physics.CollisionGroups;

public abstract class Surface {
    protected Node surfaceNode;
    protected RigidBodyControl physicsControl;
    protected final Application app;

    public Surface(String name, Application app) {
        this.app = app;
        this.surfaceNode = new Node(name);
        buildGeometry();
        physicsControl = buildPhysicsControl();
        if (physicsControl != null) {
            physicsControl.setCollisionGroup(CollisionGroups.PLATFORM);
            physicsControl.setCollideWithGroups(CollisionGroups.BALL);
            surfaceNode.addControl(physicsControl);
        }
    }

    protected abstract void buildGeometry();
    protected abstract RigidBodyControl buildPhysicsControl();

    public void attachToWorld(Node worldNode, PhysicsSpace space) {
        worldNode.attachChild(surfaceNode);
        physicsControl.setPhysicsLocation(surfaceNode.getLocalTranslation());
        physicsControl.setPhysicsRotation(surfaceNode.getLocalRotation());
        space.add(physicsControl);
    }

    public void detachFromWorld(Node worldNode, PhysicsSpace space) {
        worldNode.detachChild(surfaceNode);
        space.remove(physicsControl);
    }

    public void setPosition(Vector3f position) {
        surfaceNode.setLocalTranslation(position);
    }

    public void setRotation(Quaternion rotation) {
        surfaceNode.setLocalRotation(rotation);
    }

    public void applyGhostMaterial(AssetManager assetManager, ColorRGBA color) {
        Material ghostMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        ghostMat.setColor("Color", color);
        ghostMat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        surfaceNode.breadthFirstTraversal(new SceneGraphVisitor() {
            @Override
            public void visit(Spatial s) {
                if (s instanceof Geometry) {
                    ((Geometry) s).setMaterial(ghostMat);
                    s.setQueueBucket(RenderQueue.Bucket.Transparent);
                }
            }
        });
    }

    public Node getNode() { return surfaceNode; }
    public Vector3f getPosition() { return surfaceNode.getWorldTranslation(); }
}
