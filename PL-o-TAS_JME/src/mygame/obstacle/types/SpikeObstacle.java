package mygame.obstacle.types;

import com.jme3.app.Application;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import mygame.obstacle.Obstacle;
import mygame.physics.CollisionGroups;

public class SpikeObstacle extends Obstacle {
    private static final Vector3f HALF = new Vector3f(0.4f, 0.8f, 0.4f);

    public SpikeObstacle(Application app) {
        super("SpikeObstacle", app);
    }

    @Override
    protected void buildGeometry() {
        Geometry geom = new Geometry("SpikeGeom", new Box(HALF.x, HALF.y, HALF.z));
        Material mat = new Material(app.getAssetManager(), "Common/MatDefs/Light/Lighting.j3md");
        mat.setBoolean("UseMaterialColors", true);
        mat.setColor("Diffuse", ColorRGBA.Red);
        mat.setColor("Ambient", new ColorRGBA(0.5f, 0f, 0f, 1f));
        geom.setMaterial(mat);
        obstacleNode.attachChild(geom);
    }

    @Override
    protected void buildPhysics() {
        RigidBodyControl rbc = new RigidBodyControl(new BoxCollisionShape(HALF), 0f);
        rbc.setCollisionGroup(CollisionGroups.OBSTACLE);
        rbc.setCollideWithGroups(CollisionGroups.BALL);
        obstacleNode.addControl(rbc);
        lethalBodies.add(rbc);
    }
}
