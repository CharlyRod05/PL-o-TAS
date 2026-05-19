package mygame.obstacle.types;

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
import mygame.obstacle.Obstacle;
import mygame.physics.CollisionGroups;

public class RotatingObstacle extends Obstacle {
    private static final Vector3f HALF = new Vector3f(3f, 0.2f, 0.4f);
    private final float rotSpeed;
    private float totalAngle = 0f;
    private RigidBodyControl rbc;

    public RotatingObstacle(Application app, float rotSpeedDegPerSec) {
        super("RotatingObstacle", app);
        this.rotSpeed = rotSpeedDegPerSec * FastMath.DEG_TO_RAD;
    }

    @Override
    protected void buildGeometry() {
        Geometry geom = new Geometry("RotatingGeom", new Box(HALF.x, HALF.y, HALF.z));
        Material mat = new Material(app.getAssetManager(), "Common/MatDefs/Light/Lighting.j3md");
        mat.setBoolean("UseMaterialColors", true);
        mat.setColor("Diffuse", new ColorRGBA(0.6f, 0f, 0.8f, 1f));
        mat.setColor("Ambient", new ColorRGBA(0.3f, 0f, 0.4f, 1f));
        geom.setMaterial(mat);
        obstacleNode.attachChild(geom);
    }

    @Override
    protected void buildPhysics() {
        rbc = new RigidBodyControl(new BoxCollisionShape(HALF), 1f);
        rbc.setKinematic(true);
        rbc.setCollisionGroup(CollisionGroups.OBSTACLE);
        rbc.setCollideWithGroups(CollisionGroups.BALL);
        obstacleNode.addControl(rbc);
        lethalBodies.add(rbc);
    }

    @Override
    public void update(float tpf) {
        totalAngle += rotSpeed * tpf;
        Quaternion rot = new Quaternion();
        rot.fromAngleAxis(totalAngle, Vector3f.UNIT_Y);
        rbc.setPhysicsRotation(rot);
        obstacleNode.setLocalRotation(rot);
    }
}
