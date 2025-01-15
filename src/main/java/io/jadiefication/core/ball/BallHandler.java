package io.jadiefication.core.ball;

import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.particle.Particle;
import net.minestom.server.timer.Scheduler;
import net.minestom.server.timer.Task;
import net.minestom.server.timer.TaskSchedule;
import io.jadiefication.core.Handler;
import io.jadiefication.particlegenerator.ParticleGenerator;

import java.util.List;
import java.util.Set;

/**
 * Hate Math
 *
 * @Author: Jade
 */

public sealed interface BallHandler extends Handler permits BladeBall {

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

        BallState.findFirstTarget(container);
        BallState.isActive = true;
        BallState.firstTarget = true;
    }

    void start(InstanceContainer container);


    default Player findTarget(InstanceContainer container) {
        double width = 0.6;
        double height = 1.8;

        Player blockingPlayer = BallState.playerWhomHitTheBall;
        Vec start = blockingPlayer.getPosition().asVec();
        Vec directionVec = blockingPlayer.getPosition().direction();

        Player closestPlayer = null;
        double closestDistance = Double.MAX_VALUE;
        boolean foundPlayerInRay = false;

        for (Player player : container.getPlayers()) {
            if (player.equals(blockingPlayer) || player.getGameMode().equals(GameMode.SPECTATOR)) continue;

            // Calculate player AABB (similar to before)
            Pos playerPos = player.getPosition();
            Vec playerMin = new Vec(playerPos.x() - width / 2, playerPos.y(), playerPos.z() - width / 2);
            Vec playerMax = new Vec(playerPos.x() + width / 2, playerPos.y() + height, playerPos.z() + width / 2);

            // Ray-AABB intersection
            double tMinX = (playerMin.x() - start.x()) / directionVec.x();
            double tMaxX = (playerMax.x() - start.x()) / directionVec.x();
            if (directionVec.x() < 0) {
                double temp = tMinX;
                tMinX = tMaxX;
                tMaxX = temp;
            }

            double tMinY = (playerMin.y() - start.y()) / directionVec.y();
            double tMaxY = (playerMax.y() - start.y()) / directionVec.y();
            if (directionVec.y() < 0) {
                double temp = tMinY;
                tMinY = tMaxY;
                tMaxY = temp;
            }

            double tMinZ = (playerMin.z() - start.z()) / directionVec.z();
            double tMaxZ = (playerMax.z() - start.z()) / directionVec.z();
            if (directionVec.z() < 0) {
                double temp = tMinZ;
                tMinZ = tMaxZ;
                tMaxZ = temp;
            }

            double tMin = Math.max(tMinX, Math.max(tMinY, tMinZ));
            double tMax = Math.min(tMaxX, Math.min(tMaxY, tMaxZ));

            if (tMin <= tMax && tMax > 0) {
                foundPlayerInRay = true;
                double distance = start.distance(playerPos.asVec());
                if (distance < closestDistance) {
                    closestDistance = distance;
                    closestPlayer = player;

                }
            }
        }

        if (!foundPlayerInRay) {
            for (Player player : container.getPlayers()) {
                if (player.equals(blockingPlayer) || player.getGameMode().equals(GameMode.SPECTATOR)) continue;

                Vec playerPos = player.getPosition().asVec();

                // Calculate distance from player to the ray
                Vec playerToRayStart = playerPos.sub(start);
                Vec projection = directionVec.mul(playerToRayStart.dot(directionVec));
                Vec closestPointOnRay = start.add(projection);
                double distanceToRay = playerPos.distance(closestPointOnRay);

                if (distanceToRay < closestDistance) {
                    closestDistance = distanceToRay;
                    closestPlayer = player;
                }
            }
        }

        BladeBall.hasPlayer = true;
        return closestPlayer;

    }

    void update(InstanceContainer container);

    class BallState {
        public static Player playerWhomHitTheBall = null;
        public static boolean isActive = false;
        public static boolean stayingStill = true;
        public static List<List<Task>> tasks;
        public static Pos ballPosition = new Pos(0.5, 45.0, 0.5);
        public static int dt;
        public final static Task dtCounter = MinecraftServer.getSchedulerManager().scheduleTask(() -> {
            BallState.dt++;
            return TaskSchedule.tick(1);
        }, TaskSchedule.tick(1));;
        public static boolean firstTarget = true;

        public static Player findFirstTarget(InstanceContainer container) {
            Set<Player> players = container.getPlayers();

            Player closestPlayer = null;
            double closestDistance = Double.MAX_VALUE;

            for (Player player : players) {
                if (player.getGameMode().equals(GameMode.SPECTATOR)) continue;
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
