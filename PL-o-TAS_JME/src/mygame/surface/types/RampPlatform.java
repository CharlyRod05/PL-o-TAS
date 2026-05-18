package mygame.surface.types;

import com.jme3.app.Application;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import mygame.surface.Surface;

public class RampPlatform extends Surface {
    private static final float[] ANGLES_DEG = {0f, 30f, 45f, 60f};
    private static final Vector3f HALF = new Vector3f(2f, 0.1f, 1f);
    private int angleIndex = 1;

    public RampPlatform(Application app) {
        super("RampPlatform", app);
        applyAngle();
    }

    @Override
    protected void buildGeometry() {
        Geometry geom = new Geometry("RampGeom", new Box(HALF.x, HALF.y, HALF.z));
        Material mat = new Material(app.getAssetManager(), "Common/MatDefs/Light/Lighting.j3md");
        mat.setBoolean("UseMaterialColors", true);
        mat.setColor("Diffuse", new ColorRGBA(0.4f, 1f, 0.5f, 1f));
        mat.setColor("Ambient", new ColorRGBA(0.2f, 0.5f, 0.2f, 1f));
        geom.setMaterial(mat);
        surfaceNode.attachChild(geom);
    }

    @Override
    protected RigidBodyControl buildPhysicsControl() {
        return new RigidBodyControl(new BoxCollisionShape(HALF), 0f);
    }

    public void nextAngle() {
        angleIndex = (angleIndex + 1) % ANGLES_DEG.length;
        applyAngle();
    }

    public float getCurrentAngleDeg() {
        return ANGLES_DEG[angleIndex];
    }

    private void applyAngle() {
        Quaternion rot = new Quaternion();
        rot.fromAngleAxis(ANGLES_DEG[angleIndex] * FastMath.DEG_TO_RAD, Vector3f.UNIT_X);
        setRotation(rot);
    }
}
