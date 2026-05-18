package mygame.obstacle.types;

import com.jme3.app.Application;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.control.GhostControl;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import mygame.obstacle.Obstacle;

public class HazardZone extends Obstacle {
    private static final Vector3f HALF = new Vector3f(2f, 1f, 2f);
    private GhostControl ghostControl;

    public HazardZone(Application app) {
        super("HazardZone", app);
    }

    @Override
    protected void buildGeometry() {
        Geometry geom = new Geometry("HazardGeom", new Box(HALF.x, HALF.y, HALF.z));
        Material mat = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", new ColorRGBA(1f, 0f, 0f, 0.3f));
        mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        geom.setMaterial(mat);
        geom.setQueueBucket(RenderQueue.Bucket.Transparent);
        obstacleNode.attachChild(geom);
    }

    @Override
    protected void buildPhysics() {
        ghostControl = new GhostControl(new BoxCollisionShape(HALF));
        obstacleNode.addControl(ghostControl);
    }

    @Override
    public GhostControl getGhostControl() {
        return ghostControl;
    }
}
