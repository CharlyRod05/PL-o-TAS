package mygame.player;

import com.jme3.anim.AnimComposer;
import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import mygame.physics.CollisionGroups;

/**
 *
 * @author rodri
 */
public class Player {
    private Node playerNode = new Node("Player");
    private SimpleApplication app;
    private boolean moveForward, moveBackward, moveLeft, moveRight;
    private float speed = 0.1f;
    private float cameraYaw = 0f;
    Spatial model;
    CharacterControl control;
    private AnimControl animControl;
    private AnimChannel animChannel;
    private String currentAnim = "";
    
    
    public Player(Application app, BulletAppState bullet) {
        this.app = (SimpleApplication) app;
        
        
        //Visual personaje
        Spatial model = this.app.getAssetManager().loadModel("Models/Jaime/Jaime.j3o");
        model.setLocalScale(1f);
        model.setLocalTranslation(0f, -1.5f, 0f); // ajusta al centro de la cápsula
        playerNode.attachChild(model);
        
        animControl = findAnimControl((Node) model);
        if (animControl != null) {
            animChannel = animControl.createChannel();
            animChannel.setAnim("Idle");
            // Imprime las animaciones disponibles
            System.out.println("Animaciones: " + animControl.getAnimationNames());
        } else {
            System.out.println("No tiene AnimControl tampoco");
        }
        //FISICA personaje
        CapsuleCollisionShape shape = new CapsuleCollisionShape(0.5f, 1.8f);

        control = new CharacterControl(shape, 0.05f);

        control.setJumpSpeed(10);
        control.setFallSpeed(20);
        control.setGravity(30);

        control.setPhysicsLocation(new Vector3f(0, 5, 0));
        
        control.setCollisionGroup(CollisionGroups.WORLD);
        control.setCollideWithGroups(CollisionGroups.WORLD | CollisionGroups.BALL);
        
        playerNode.addControl(control);
        bullet.getPhysicsSpace().add(control);
        
        
    }
    
    public void update() {

        Vector3f direction = new Vector3f();

        // Vectores relativos a donde mira la camara
        float sinY = FastMath.sin(cameraYaw);
        float cosY = FastMath.cos(cameraYaw);

        Vector3f forward = new Vector3f(sinY, 0f, -cosY);
        Vector3f right = new Vector3f(cosY, 0f, sinY);

        if (moveForward) {
            direction.addLocal(forward);
        }
        if (moveBackward) {
            direction.addLocal(forward.negate());
        }
        if (moveRight) {
            direction.addLocal(right);
        }
        if (moveLeft) {
            direction.addLocal(right.negate());
        }

        if (direction.lengthSquared() > 0f) {
            direction.normalizeLocal();
        }
        
        boolean isMoving = moveForward || moveBackward || moveLeft || moveRight;
        boolean isOnGround = control.onGround();

        // Lógica de animaciones
        if (!isOnGround) {
            playAnimation("JumpStart");
        } else if (isMoving) {
            playAnimation("Walk");
        } else {
            playAnimation("Idle");
        }

        control.setWalkDirection(direction.multLocal(speed));
    }
    public Vector3f getPosition() {
        return control.getPhysicsLocation();
    }
    
    public void jump() {
        control.jump();
    }

    public Node getNode() {
        return playerNode;
    }
    
    public void setCameraYaw(float yaw) {
        this.cameraYaw = yaw;
    }
    
    public void setForward(boolean value) {
        moveForward = value;
    }

    public void setBackward(boolean value) {
        moveBackward = value;
    }

    public void setLeft(boolean value) {
        moveLeft = value;
    }

    public void setRight(boolean value) {
        moveRight = value;
    }

    private void playAnimation(String name) {
        if (currentAnim.equals(name) || animChannel == null) {
            return;
        }
        currentAnim = name;
        animChannel.setAnim(name);
    }
    
    private AnimControl findAnimControl(Node node) {
        AnimControl control = node.getControl(AnimControl.class);
        if (control != null) {
            return control;
        }

        for (Spatial child : node.getChildren()) {
            if (child instanceof Node) {
                AnimControl found = findAnimControl((Node) child);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }
}
