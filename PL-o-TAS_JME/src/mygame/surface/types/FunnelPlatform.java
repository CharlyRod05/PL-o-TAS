package mygame.surface.types;

import com.jme3.app.Application;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CompoundCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import mygame.surface.Surface;

public class FunnelPlatform extends Surface {

    private static final float ANGLE = 40f;

    private static final Vector3f WING_HALF =
            new Vector3f(1.5f, 0.1f, 1f);
    
    private static final float HEIGHT_OFFSET = 0.75f;

    private static final Vector3f LEFT_OFFSET
            = new Vector3f(-1.5f, HEIGHT_OFFSET, 0f);

    private static final Vector3f RIGHT_OFFSET
            = new Vector3f(1.5f, HEIGHT_OFFSET, 0f);

    public FunnelPlatform(Application app) {
        super("FunnelPlatform", app);
    }

    @Override
    protected void buildGeometry() {

        Material mat = new Material(
                app.getAssetManager(),
                "Common/MatDefs/Light/Lighting.j3md"
        );

        mat.setBoolean("UseMaterialColors", true);

        mat.setColor(
                "Diffuse",
                new ColorRGBA(0.8f, 0.3f, 0.8f, 1f)
        );

        mat.setColor(
                "Ambient",
                new ColorRGBA(0.4f, 0.15f, 0.4f, 1f)
        );

        /*
         * LEFT
         */
        Geometry leftGeom = new Geometry(
                "LeftGeom",
                new Box(
                        WING_HALF.x,
                        WING_HALF.y,
                        WING_HALF.z
                )
        );

        leftGeom.setMaterial(mat);

        leftGeom.setLocalTranslation(LEFT_OFFSET);

        Quaternion leftRot = new Quaternion();
        leftRot.fromAngleAxis(
                -ANGLE * FastMath.DEG_TO_RAD,
                Vector3f.UNIT_Z
        );

        leftGeom.setLocalRotation(leftRot);

        /*
         * RIGHT
         */
        Geometry rightGeom = new Geometry(
                "RightGeom",
                new Box(
                        WING_HALF.x,
                        WING_HALF.y,
                        WING_HALF.z
                )
        );

        rightGeom.setMaterial(mat);

        rightGeom.setLocalTranslation(RIGHT_OFFSET);

        Quaternion rightRot = new Quaternion();
        rightRot.fromAngleAxis(
                ANGLE * FastMath.DEG_TO_RAD,
                Vector3f.UNIT_Z
        );

        rightGeom.setLocalRotation(rightRot);

        surfaceNode.attachChild(leftGeom);
        surfaceNode.attachChild(rightGeom);
    }

    @Override
    protected RigidBodyControl buildPhysicsControl() {

        CompoundCollisionShape compound =
                new CompoundCollisionShape();

        Quaternion leftRot = new Quaternion();
        leftRot.fromAngleAxis(
                -ANGLE * FastMath.DEG_TO_RAD,
                Vector3f.UNIT_Z
        );

        Quaternion rightRot = new Quaternion();
        rightRot.fromAngleAxis(
                ANGLE * FastMath.DEG_TO_RAD,
                Vector3f.UNIT_Z
        );

        compound.addChildShape(
                new BoxCollisionShape(WING_HALF),
                new Vector3f(LEFT_OFFSET),
                leftRot.toRotationMatrix()
        );

        compound.addChildShape(
                new BoxCollisionShape(WING_HALF),
                new Vector3f(RIGHT_OFFSET),
                rightRot.toRotationMatrix()
        );

        return new RigidBodyControl(compound, 0f);
    }
}