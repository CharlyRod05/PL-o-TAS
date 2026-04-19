/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mygame.player;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;

/**
 *
 * @author rodri
 */
public class Player {
    private Node playerNode = new Node("Player");
    private SimpleApplication app;

    public Player(Application app, BulletAppState bullet) {
        this.app = (SimpleApplication) app;
        
        
        //Visual personaje
        Box box = new Box(0.5f, 1f, 0.5f);
        Geometry geom = new Geometry("PlayerGeom", box);

        Material mat = new Material(app.getAssetManager(),
        "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Red);

        geom.setMaterial(mat);
        
        playerNode.attachChild(geom);
        
        
        //FISICA personaje
        CapsuleCollisionShape shape = new CapsuleCollisionShape(0.5f, 1.8f);

        CharacterControl control = new CharacterControl(shape, 0.05f);

        control.setJumpSpeed(10);
        control.setFallSpeed(20);
        control.setGravity(30);

        control.setPhysicsLocation(new Vector3f(0, 5, 0));
        
        playerNode.addControl(control);
        bullet.getPhysicsSpace().add(control);
        
        
playerNode.attachChild(geom);
    }

    public Node getNode() {
        return playerNode;
    }
}
