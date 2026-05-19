package mygame.level.levels;

import com.jme3.app.Application;
import com.jme3.math.Vector3f;
import mygame.goal.Goal;
import mygame.level.Level;
import mygame.obstacle.types.HazardZone;
import mygame.obstacle.types.PendulumObstacle;
import mygame.obstacle.types.RotatingObstacle;
import mygame.obstacle.types.SpikeObstacle;

public class Level3 extends Level {

    @Override
    public void setup(Application app) {
        levelName = "Nivel 3";
        spawnPoint = new Vector3f(0f, 12f, 0f);

        flatCount    = 2;
        rampCount    = 2;
        bouncerCount = 1;
        funnelCount  = 1;

        SpikeObstacle spike1 = new SpikeObstacle(app);
        spike1.setPosition(new Vector3f(-4f, 1f, -4f));
        obstacles.add(spike1);

        SpikeObstacle spike2 = new SpikeObstacle(app);
        spike2.setPosition(new Vector3f(4f, 1f, -4f));
        obstacles.add(spike2);

        PendulumObstacle pendulum = new PendulumObstacle(app,
                new Vector3f(0f, 6f, -3f), 3f, 2f);
        obstacles.add(pendulum);

        RotatingObstacle rotator = new RotatingObstacle(app, 60f);
        rotator.setPosition(new Vector3f(0f, 4f, -6f));
        obstacles.add(rotator);

        HazardZone hazard = new HazardZone(app);
        hazard.setPosition(new Vector3f(0f, 1f, -8f));
        obstacles.add(hazard);

        goal = new Goal(app);
        goal.setPosition(new Vector3f(0f, 1f, -12f));
    }
}
