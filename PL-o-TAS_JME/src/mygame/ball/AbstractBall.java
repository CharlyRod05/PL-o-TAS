/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mygame.ball;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import mygame.physics.CollisionGroups;

/**
 *
 * @author rodri
 */
public abstract class AbstractBall {

    protected Node ballNode;
    protected RigidBodyControl physicsControl;
    protected final float radius;
    protected final float mass;
    protected final AssetManager assetManager;
    
    public AbstractBall(String name, AssetManager assetManager,
            float radius, float mass) {
        this.assetManager = assetManager;
        this.radius = radius;
        this.mass = mass;
        this.ballNode = new Node(name);

        buildGeometry();
        buildPhysics();
    }
    protected abstract void buildGeometry();

    private void buildPhysics() {
        SphereCollisionShape shape = new SphereCollisionShape(radius);
        physicsControl = new RigidBodyControl(shape, mass);

        physicsControl.setRestitution(0.8f);
        physicsControl.setFriction(0.0f);

        ballNode.addControl(physicsControl);

        physicsControl.setCollisionGroup(CollisionGroups.BALL);
        physicsControl.setCollideWithGroups(CollisionGroups.WORLD);
    }

    public void addToPhysicsSpace(PhysicsSpace space) {
        space.add(physicsControl);
    }

    public void setSpawnPosition(Vector3f position) {
        physicsControl.setPhysicsLocation(position);
        physicsControl.setLinearVelocity(Vector3f.ZERO);
        physicsControl.setAngularVelocity(Vector3f.ZERO);
    }

    public void destroy(PhysicsSpace space, Node parentNode) {
        space.remove(physicsControl);
        parentNode.detachChild(ballNode);
    }

    // ── igual que tu Player ──
    public Node getNode() {
        return ballNode;
    }

    public RigidBodyControl getPhysicsControl() {
        return physicsControl;
    }

    public float getRadius() {
        return radius;
    }

    public abstract BallType getType();
}
