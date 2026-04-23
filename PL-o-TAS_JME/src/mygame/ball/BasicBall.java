/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mygame.ball;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Sphere;

/**
 *
 * @author rodri
 */
public class BasicBall extends AbstractBall{
    private static final float DEFAULT_RADIUS = 0.3f;
    private static final float DEFAULT_MASS   = 1.0f;
    
    public BasicBall(AssetManager assetManager) {
        super("BasicBall", assetManager, DEFAULT_RADIUS, DEFAULT_MASS);
    }
    
    @Override
    protected void buildGeometry() {
        Sphere mesh = new Sphere(20, 20, radius);
        mesh.setTextureMode(Sphere.TextureMode.Projected);

        Geometry geom = new Geometry("BasicBallGeom", mesh);

        Material mat = new Material(assetManager,
                "Common/MatDefs/Light/Lighting.j3md");
        mat.setBoolean("UseMaterialColors", true);
        mat.setColor("Diffuse",  ColorRGBA.Blue);
        mat.setColor("Ambient",  ColorRGBA.Blue.mult(0.3f));
        mat.setColor("Specular", ColorRGBA.White);
        mat.setFloat("Shininess", 64f);
        geom.setMaterial(mat);

        ballNode.attachChild(geom); // ← ballNode interno, como playerNode en tu Player
    }

    @Override
    public BallType getType() { return BallType.BASIC; }
}

