package mygame.level.levels;

import com.jme3.app.Application;
import com.jme3.math.Vector3f;
import mygame.goal.Goal;
import mygame.level.Level;
import mygame.obstacle.types.PendulumObstacle;
import mygame.obstacle.types.SpikeObstacle;

public class Level2 extends Level {

    @Override
    public void setup(Application app) {
        levelName = "Nivel 2";
        spawnPoint = new Vector3f(0f, 12f, 0f);

        flatCount    = 3;
        rampCount    = 2;
        bouncerCount = 1;
        funnelCount  = 0;

        SpikeObstacle spike1 = new SpikeObstacle(app);
        spike1.setPosition(new Vector3f(3f, 1f, 3f));
        obstacles.add(spike1);

        SpikeObstacle spike2 = new SpikeObstacle(app);
        spike2.setPosition(new Vector3f(-3f, 1f, 3f));
        obstacles.add(spike2);

        SpikeObstacle spike3 = new SpikeObstacle(app);
        spike3.setPosition(new Vector3f(3f, 1f, 7f));
        obstacles.add(spike3);

        PendulumObstacle pendulum = new PendulumObstacle(app,
                new Vector3f(0f, 5f, 5f), 4f, 1.5f);
        obstacles.add(pendulum);

        goal = new Goal(app);
        goal.setPosition(new Vector3f(0f, 1f, 10f));
    }
}
