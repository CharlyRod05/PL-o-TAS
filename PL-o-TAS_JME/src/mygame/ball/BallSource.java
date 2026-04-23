/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package mygame.ball;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author rodri
 */
public class BallSource {
    private final Node sourceNode;          // ← interno, como playerNode
    private final BallFactory  factory;
    private final Node         parentNode;
    private final PhysicsSpace physicsSpace;

    private BallType ballType;
    private int      maxBalls      = 10;
    private boolean  autoSpawn     = false;
    private float    spawnInterval = 2.0f;

    private final List<AbstractBall> activeBalls    = new ArrayList<>();
    private float timeSinceLastSpawn = 0f;
    
    public BallSource(String name,
            BallFactory factory,
            Node worldNode,
            PhysicsSpace physicsSpace,
            BallType ballType) {
        this.sourceNode = new Node(name);
        this.factory = factory;
        this.parentNode = worldNode;
        this.physicsSpace = physicsSpace;
        this.ballType = ballType;
    }
    
    
    public AbstractBall spawnBall() {
        if (activeBalls.size() >= maxBalls) {
            return null;
        }

        AbstractBall ball = factory.create(ballType);

        parentNode.attachChild(ball.getNode());
        ball.addToPhysicsSpace(physicsSpace);

        // Toma la posición mundial del sourceNode
        Vector3f spawnPos = sourceNode.getWorldTranslation().clone();
        ball.setSpawnPosition(spawnPos);

        activeBalls.add(ball);
        return ball;
    }
    public void destroyBall(AbstractBall ball) {
        ball.destroy(physicsSpace, parentNode);
        activeBalls.remove(ball);    
    }
    public void destroyAll() {
        Iterator<AbstractBall> it = activeBalls.iterator();
        while (it.hasNext()) {
            it.next().destroy(physicsSpace, parentNode);
            it.remove();
        }
    }

    public void update(float tpf) {
        if (!autoSpawn) {
            return;
        }
        timeSinceLastSpawn += tpf;
        if (timeSinceLastSpawn >= spawnInterval) {
            spawnBall();
            timeSinceLastSpawn = 0f;
        }
    }

    public Node getNode() {
        return sourceNode;
    }

    public BallSource setAutoSpawn(boolean enabled, float intervalSeconds) {
        this.autoSpawn = enabled;
        this.spawnInterval = intervalSeconds;
        timeSinceLastSpawn = 0f;
        return this;
    }

    public BallSource setMaxBalls(int max) {
        this.maxBalls = max;
        return this;
    }

    public BallSource setBallType(BallType type) {
        this.ballType = type;
        return this;
    }

    public int getActiveBallCount() {
        return activeBalls.size();
    }

    public List<AbstractBall> getActiveBalls() {
        return activeBalls;
    }
}

