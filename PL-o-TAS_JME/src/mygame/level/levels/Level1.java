package mygame.level.levels;

import com.jme3.app.Application;
import com.jme3.math.Vector3f;
import mygame.goal.Goal;
import mygame.level.Level;
import mygame.obstacle.types.SpikeObstacle;

public class Level1 extends Level {

    @Override
    public void setup(Application app) {
        levelName = "Nivel 1";
        spawnPoint = new Vector3f(0f, 15f, 0f);

        flatCount    = 4;
        rampCount    = 2;
        bouncerCount = 0;
        funnelCount  = 0;

        SpikeObstacle spike1 = new SpikeObstacle(app);
        spike1.setPosition(new Vector3f(4f, 1f, 0f));
        obstacles.add(spike1);

        SpikeObstacle spike2 = new SpikeObstacle(app);
        spike2.setPosition(new Vector3f(8f, 1f, 0f));
        obstacles.add(spike2);

        goal = new Goal(app);
        goal.setPosition(new Vector3f(12f, 1f, 0f));
    }
}
