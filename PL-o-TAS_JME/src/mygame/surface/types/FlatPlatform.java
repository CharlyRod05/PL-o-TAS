package mygame.surface.types;

import com.jme3.app.Application;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import mygame.surface.Surface;

public class FlatPlatform extends Surface {
    public static final Vector3f HALF = new Vector3f(2f, 0.1f, 1f);

    public FlatPlatform(Application app) {
        super("FlatPlatform", app);
    }

    @Override
    protected void buildGeometry() {
        Geometry geom = new Geometry("FlatGeom", new Box(HALF.x, HALF.y, HALF.z));
        Material mat = new Material(app.getAssetManager(), "Common/MatDefs/Light/Lighting.j3md");
        mat.setBoolean("UseMaterialColors", true);
        mat.setColor("Diffuse", new ColorRGBA(0.4f, 0.6f, 1f, 1f));
        mat.setColor("Ambient", new ColorRGBA(0.2f, 0.3f, 0.5f, 1f));
        geom.setMaterial(mat);
        surfaceNode.attachChild(geom);
    }

    @Override
    protected RigidBodyControl buildPhysicsControl() {
        return new RigidBodyControl(new BoxCollisionShape(HALF), 0f);
    }
}
