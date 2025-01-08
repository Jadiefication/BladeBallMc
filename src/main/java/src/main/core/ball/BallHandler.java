package src.main.core.ball;

import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.particle.Particle;
import net.minestom.server.timer.Scheduler;
import net.minestom.server.timer.Task;
import net.minestom.server.timer.TaskSchedule;
import src.main.particlegenerator.ParticleGenerator;

import java.util.List;
import java.util.Set;

/**
 * Hate Math
 *
 * @Author: Jade
 */

public sealed interface BallHandler permits BladeBall {

    static void restart(InstanceContainer container) {

        BallState.tasks.forEach(tasks -> tasks.forEach(Task::cancel));
        BallState.isActive = false;

        Scheduler scheduler = MinecraftServer.getSchedulerManager();

        scheduler.scheduleTask(() -> {
            if (!BallState.stayingStill) {
                // Stop spawning particles when no longer staying still
                return TaskSchedule.stop();
            }
            BallState.tasks = ParticleGenerator.spawnSphereParticles(container, BallState.ballPosition, 0.5, 0.5, 0.5, Particle.WAX_OFF, 1);
            return TaskSchedule.tick(1);
        }, TaskSchedule.tick(1));

        BallState.dtCounter = scheduler.scheduleTask(() -> {
            BallState.dt++;
            return TaskSchedule.tick(1);
        }, TaskSchedule.tick(1));

        BallState.findFirstTarget(container);
        BallState.isActive = true;
        BallState.firstTarget = true;
    }


    default void findTarget(InstanceContainer container) {
        Set<Player> players = container.getPlayers();


    }

    void update(InstanceContainer container);

    class BallState {
        public static boolean isActive = false;
        public static boolean stayingStill = true;
        public static List<List<Task>> tasks;
        public static Pos ballPosition = new Pos(0.0, 43.0, 0.0);
        public static int dt;
        public static Task dtCounter;
        public static boolean firstTarget;

        public static Player findFirstTarget(InstanceContainer container) {
            Set<Player> players = container.getPlayers();

            Player closestPlayer = null;
            double closestDistance = Double.MAX_VALUE;

            for (Player player : players) {
                double distance = player.getPosition().distance(ballPosition);
                if (distance < closestDistance) {
                    closestDistance = distance;
                    closestPlayer = player;
                }
            }

            stayingStill = false;

            return closestPlayer;
        }
    }
}
