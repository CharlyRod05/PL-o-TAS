package mygame.level;

import com.jme3.app.Application;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import java.util.ArrayList;
import java.util.List;
import mygame.goal.Goal;
import mygame.obstacle.Obstacle;

public abstract class Level {
    protected Vector3f spawnPoint;
    protected List<Obstacle> obstacles = new ArrayList<>();
    protected Goal goal;

    protected int flatCount;
    protected int rampCount;
    protected int bouncerCount;
    protected int funnelCount;

    protected String levelName;

    public abstract void setup(Application app);

    public void attachToWorld(Node worldNode, PhysicsSpace space) {
        for (Obstacle o : obstacles) o.attachToWorld(worldNode, space);
        goal.attachToWorld(worldNode, space);
    }

    public void detachFromWorld(Node worldNode, PhysicsSpace space) {
        for (Obstacle o : obstacles) o.detachFromWorld(worldNode, space);
        goal.detachFromWorld(worldNode, space);
    }

    public void update(float tpf) {
        for (Obstacle o : obstacles) o.update(tpf);
    }

    public Vector3f getSpawnPoint()      { return spawnPoint; }
    public List<Obstacle> getObstacles() { return obstacles; }
    public Goal getGoal()                { return goal; }
    public int getFlatCount()            { return flatCount; }
    public int getRampCount()            { return rampCount; }
    public int getBouncerCount()         { return bouncerCount; }
    public int getFunnelCount()          { return funnelCount; }
    public String getLevelName()         { return levelName; }
}
