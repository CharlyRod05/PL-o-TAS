package mygame.obstacle.types;

import com.jme3.app.Application;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import mygame.obstacle.Obstacle;
import mygame.physics.CollisionGroups;

public class PendulumObstacle extends Obstacle {
    private static final Vector3f HALF = new Vector3f(1.5f, 0.2f, 0.5f);
    private final Vector3f pivot;
    private final float amplitude;
    private final float speed;
    private float time = 0f;
    private RigidBodyControl rbc;

    public PendulumObstacle(Application app, Vector3f pivot, float amplitude, float speed) {
        super("PendulumObstacle", app);
        this.pivot = pivot.clone();
        this.amplitude = amplitude;
        this.speed = speed;
        obstacleNode.setLocalTranslation(pivot);
    }

    @Override
    protected void buildGeometry() {
        Geometry geom = new Geometry("PendulumGeom", new Box(HALF.x, HALF.y, HALF.z));
        Material mat = new Material(app.getAssetManager(), "Common/MatDefs/Light/Lighting.j3md");
        mat.setBoolean("UseMaterialColors", true);
        mat.setColor("Diffuse", new ColorRGBA(0.8f, 0.6f, 0f, 1f));
        mat.setColor("Ambient", new ColorRGBA(0.4f, 0.3f, 0f, 1f));
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
        time += tpf;
        float offsetX = amplitude * FastMath.sin(time * speed);
        Vector3f newPos = pivot.add(offsetX, 0f, 0f);
        rbc.setPhysicsLocation(newPos);
        obstacleNode.setLocalTranslation(newPos);
    }
}
