package mygame.surface.types;

import com.jme3.app.Application;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import mygame.physics.CollisionGroups;
import mygame.surface.Surface;

public class FunnelPlatform extends Surface {
    private static final Vector3f WING_HALF = new Vector3f(1.5f, 0.1f, 1f);
    private RigidBodyControl leftCtrl, rightCtrl;

    public FunnelPlatform(Application app) {
        super("FunnelPlatform", app);
    }

    @Override
    protected void buildGeometry() {
        Material mat = new Material(app.getAssetManager(), "Common/MatDefs/Light/Lighting.j3md");
        mat.setBoolean("UseMaterialColors", true);
        mat.setColor("Diffuse", new ColorRGBA(0.8f, 0.3f, 0.8f, 1f));
        mat.setColor("Ambient", new ColorRGBA(0.4f, 0.15f, 0.4f, 1f));

        Quaternion leftRot = new Quaternion();
        leftRot.fromAngleAxis(-40f * FastMath.DEG_TO_RAD, Vector3f.UNIT_Z);
        Node leftWing = new Node("LeftWing");
        leftWing.setLocalRotation(leftRot);
        leftWing.setLocalTranslation(-1.5f, 0f, 0f);
        Geometry lg = new Geometry("LeftGeom", new Box(WING_HALF.x, WING_HALF.y, WING_HALF.z));
        lg.setMaterial(mat);
        leftWing.attachChild(lg);

        Quaternion rightRot = new Quaternion();
        rightRot.fromAngleAxis(40f * FastMath.DEG_TO_RAD, Vector3f.UNIT_Z);
        Node rightWing = new Node("RightWing");
        rightWing.setLocalRotation(rightRot);
        rightWing.setLocalTranslation(1.5f, 0f, 0f);
        Geometry rg = new Geometry("RightGeom", new Box(WING_HALF.x, WING_HALF.y, WING_HALF.z));
        rg.setMaterial(mat);
        rightWing.attachChild(rg);

        surfaceNode.attachChild(leftWing);
        surfaceNode.attachChild(rightWing);

        leftCtrl = makeWingCtrl();
        rightCtrl = makeWingCtrl();
        leftWing.addControl(leftCtrl);
        rightWing.addControl(rightCtrl);
    }

    private RigidBodyControl makeWingCtrl() {
        RigidBodyControl rbc = new RigidBodyControl(new BoxCollisionShape(WING_HALF), 0f);
        rbc.setCollisionGroup(CollisionGroups.PLATFORM);
        rbc.setCollideWithGroups(CollisionGroups.BALL);
        return rbc;
    }

    @Override
    protected RigidBodyControl buildPhysicsControl() {
        return null;
    }

    @Override
    public void attachToWorld(Node worldNode, PhysicsSpace space) {
        worldNode.attachChild(surfaceNode);
        space.add(leftCtrl);
        space.add(rightCtrl);
    }

    @Override
    public void detachFromWorld(Node worldNode, PhysicsSpace space) {
        worldNode.detachChild(surfaceNode);
        space.remove(leftCtrl);
        space.remove(rightCtrl);
    }
}
