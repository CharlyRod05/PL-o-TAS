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

public class BouncerPlatform extends Surface {
    private static final Vector3f HALF = new Vector3f(2f, 0.1f, 1f);

    public BouncerPlatform(Application app) {
        super("BouncerPlatform", app);
    }

    @Override
    protected void buildGeometry() {
        Geometry geom = new Geometry("BouncerGeom", new Box(HALF.x, HALF.y, HALF.z));
        Material mat = new Material(app.getAssetManager(), "Common/MatDefs/Light/Lighting.j3md");
        mat.setBoolean("UseMaterialColors", true);
        mat.setColor("Diffuse", new ColorRGBA(1f, 0.5f, 0f, 1f));
        mat.setColor("Ambient", new ColorRGBA(0.5f, 0.25f, 0f, 1f));
        geom.setMaterial(mat);
        surfaceNode.attachChild(geom);
    }

    @Override
    protected RigidBodyControl buildPhysicsControl() {
        RigidBodyControl rbc = new RigidBodyControl(new BoxCollisionShape(HALF), 0f);
        rbc.setRestitution(2.0f);
        return rbc;
    }
}
