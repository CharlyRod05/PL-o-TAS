/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mygame.world;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AppStateManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import mygame.physics.CollisionGroups;

/**
 *
 * @author rodri
 */
public class World {
    private Node worldNode = new Node("World");
    private SimpleApplication app;
    private BulletAppState bulletAppState;

    public World(Application app) {
        this.app = (SimpleApplication) app;
        
        initPhysics();
        
        //Crea el suelo del mundo
        createFloor();
        
        

        
    }

    public Node getNode() {
        return worldNode;
    }
    
    private void initPhysics() {
        bulletAppState = new BulletAppState();
        
        AppStateManager stateManager = app.getStateManager();
        stateManager.attach(bulletAppState);
    }
    
    private void createFloor(){
        //Se crea el objeto VISUAL del suelo, sin fisica
        Box box = new Box(100f, 0.1f, 100f);
        Geometry floor = new Geometry("floor", box);
        Material mat1 = new Material(app.getAssetManager(),"Common/MatDefs/Misc/Unshaded.j3md");
        mat1.setColor("Color", ColorRGBA.White);
        floor.setMaterial(mat1);
        floor.setLocalTranslation(0, 0, 0);
        
        

        //Física (masa 0 = estático)
        RigidBodyControl floorPhysics = new RigidBodyControl(0);
        floorPhysics.setCollisionGroup(CollisionGroups.WORLD);
        floorPhysics.setCollideWithGroups(CollisionGroups.WORLD | CollisionGroups.BALL);
        //Se añade la fisica al objeto visual
        
        floor.addControl(floorPhysics);

        //Registrar en Bullet
        bulletAppState.getPhysicsSpace().add(floorPhysics);
        floorPhysics.setRestitution(0.8f);

        //Agregar al mundo
        worldNode.attachChild(floor);
    }
    public BulletAppState getPhysics(){
        return bulletAppState;
    }
}
