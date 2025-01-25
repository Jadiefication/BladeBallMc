package io.jadiefication.particlegenerator;

import io.jadiefication.Nimoh;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.Instance;
import net.minestom.server.network.packet.server.play.ParticlePacket;
import net.minestom.server.particle.Particle;
import net.minestom.server.scoreboard.Team;
import net.minestom.server.timer.Scheduler;
import net.minestom.server.timer.Task;
import net.minestom.server.timer.TaskSchedule;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ParticleGenerator {

    private static final Scheduler scheduler = Nimoh.scheduler;

    private static void sendPacketsToSpecificPlayers(Vec start, Vec end, Map<Particle, Team> players) {
        int particles = 20;
        ParticlePacket[] particleNormalPackets = new ParticlePacket[particles];
        ParticlePacket[] particleOrangePackets = new ParticlePacket[particles];
        for (int j = 0; j < particles; j++) {
            double progress = (double) j / particles;
            double x = start.x() + (end.x() - start.x()) * progress;
            double z = start.z() + (end.z() - start.z()) * progress;
            double y = start.y();

            int finalJ = j;
            players.forEach((key, value) -> {
                ParticlePacket packet = new ParticlePacket(
                        key,
                        false,
                        x, y, z,
                        0f, 0f, 0f,
                        0f,
                        1
                );
                if (key.equals(Particle.WAX_ON)) {
                    particleNormalPackets[finalJ] = packet;
                } else {
                    particleOrangePackets[finalJ] = packet;
                }
            });

        }

        players.forEach((key, value) -> {
            if (key.equals(Particle.WAX_ON)) {
                value.getPlayers().forEach(player ->
                        player.sendPackets(particleNormalPackets)
                );
            } else {
                value.getPlayers().forEach(player ->
                        player.sendPackets(particleOrangePackets)
                );
            }
        });
    }

    private static void sendPackets(Instance instance, Vec start, Vec end, Particle particle) {
        int particles = 20;
        //List<Task> particleTasks = new ArrayList<>();
        ParticlePacket[] particlePackets = new ParticlePacket[particles];
        for (int j = 0; j < particles; j++) {
            double progress = (double) j / particles;
            double x = start.x() + (end.x() - start.x()) * progress;
            double z = start.z() + (end.z() - start.z()) * progress;
            double y = start.y();

            particlePackets[j] = new ParticlePacket(
                    particle,
                    false,
                    x, y, z,
                    0f, 0f, 0f,
                    0f,
                    1
            );

        }

        instance.getPlayers().forEach(
                player -> player.sendPackets(particlePackets)
        );
    }

    public static Task spawnOctagonParticles(Instance instance, Pos center, double radiusX, double radiusY, Particle particle, double duration) {
        // Calculate the 8 points of the octagon
        Vec[] points = new Vec[8];

        // Each point is 45 degrees (360/8) apart
        for (int i = 0; i < 8; i++) {
            double angle = Math.PI * i / 4; // 45 degree intervals
            double x = center.x() + radiusX * Math.cos(angle);
            double z = center.z() + radiusY * Math.sin(angle);
            points[i] = new Vec(x, center.y(), z);
        }

        // Connect the 8 points with particles
        Task particleTask = scheduler.scheduleTask(() -> {
            for (int i = 0; i < 8; i++) {
                Vec start = points[i];
                Vec end = points[(i + 1) % 8];

                sendPackets(instance, start, end, particle);
            }
        }, TaskSchedule.tick(1), TaskSchedule.tick(1));

        if (!Double.isInfinite(duration)) {
            // Schedule task cancellation
            scheduler.scheduleTask(() -> {
                particleTask.cancel();
                return TaskSchedule.nextTick();
            }, TaskSchedule.seconds((long) duration));
        }

        return particleTask;
    }

    public static Task spawnCircleParticles(Instance instance, Pos center, double radiusX, double radiusY, Particle particle, double duration) {
        Vec[] points = new Vec[32];

        for (int i = 0; i < 32; i++) {
            double angle = 2 * Math.PI * i / 32; // 11.25 degree intervals
            double x = center.x() + radiusX * Math.cos(angle);
            double z = center.z() + radiusY * Math.sin(angle);
            points[i] = new Vec(x, center.y(), z);
        }

        // Connect the 8 points with particles
        Task particleTask = scheduler.scheduleTask(() -> {
            for (int i = 0; i < 32; i++) {
                Vec start = points[i];
                Vec end = points[(i + 1) % 32];

                sendPackets(instance, start, end, particle);
            }
        }, TaskSchedule.tick(1), TaskSchedule.tick(1));

        if (!Double.isInfinite(duration)) {
            // Schedule task cancellation
            scheduler.scheduleTask(() -> {
                particleTask.cancel();
                return TaskSchedule.nextTick();
            }, TaskSchedule.seconds((long) duration));
        }

        return particleTask;
    }


    public static Task spawnSquareParticles(Instance instance, Pos center, double radiusX, double radiusY, Particle particle, double duration) {
        Vec[] points = new Vec[4];

        for (int i = 0; i < 4; i++) {
            double angle = Math.PI * i / 2;

            double x = center.x() + radiusX * Math.cos(angle);
            double z = center.z() + radiusY * Math.sin(angle);

            points[i] = new Vec(x, center.y(), z);
        }

        Task particleTask = scheduler.scheduleTask(() -> {
            for (int i = 0; i < 4; i++) {
                Vec start = points[i];
                Vec end = points[(i + 1) % 4];

                sendPackets(instance, start, end, particle);
            }
        }, TaskSchedule.tick(1), TaskSchedule.tick(1));

        if (!Double.isInfinite(duration)) {
            // Schedule task cancellation
            scheduler.scheduleTask(() -> {
                particleTask.cancel();
                return TaskSchedule.nextTick();
            }, TaskSchedule.seconds((long) duration));
        }

        return particleTask;
    }

    public static Task spawnSphereParticles(Instance instance, Pos center, double radiusX, double radiusY, double radiusZ, Particle particle, double duration) {
        double phi = Math.PI * (3 - Math.sqrt(5));
        int points = 200; // Adjust for desired density

        Task particleTask = scheduler.scheduleTask(() -> {
            for (int i = 0; i < points; i++) {
                Map.Entry<Vec, Vec> vectors = calculateSpehre(center, radiusX, radiusY, radiusZ, i, phi);

                sendPackets(instance, vectors.getKey(), vectors.getValue(), particle);
            }
        }, TaskSchedule.tick(1), TaskSchedule.tick(1));

        if (!Double.isInfinite(duration)) {
            // Schedule task cancellation
            scheduler.scheduleTask(() -> {
                particleTask.cancel();
                return TaskSchedule.nextTick();
            }, TaskSchedule.seconds((long) duration));
        }

        return particleTask;
    }

    private static Map.Entry<Vec, Vec> calculateSpehre(Pos center, double radiusX, double radiusY, double radiusZ, int i, double phi) {
        int points = 200;
        double y = 1 - (i / (double)(points - 1)) * 2;
        double radius = Math.sqrt(1 - y * y);
        double theta = phi * i;

        double x = Math.cos(theta) * radius * radiusX;
        double z = Math.sin(theta) * radius * radiusZ;
        y = y * radiusY;

        Vec start = new Vec(center.x() + x, center.y() + y, center.z() + z);

        // Calculate next point for connection
        int nextI = (i + 1) % points;
        double nextY = 1 - (nextI / (double)(points - 1)) * 2;
        double nextRadius = Math.sqrt(1 - nextY * nextY);
        double nextTheta = phi * nextI;

        double nextX = Math.cos(nextTheta) * nextRadius * radiusX;
        double nextZ = Math.sin(nextTheta) * nextRadius * radiusZ;
        nextY = nextY * radiusY;

        Vec end = new Vec(center.x() + nextX, center.y() + nextY, center.z() + nextZ);

        return Map.entry(start, end);
    }

    public static Task spawnSphereParticles(Pos center, double radiusX, double radiusY, double radiusZ, Map<Particle, Team> players, double duration) {
        double phi = Math.PI * (3 - Math.sqrt(5));
        int points = 200; // Adjust for desired density

        Task particleTask = scheduler.scheduleTask(() -> {
            for (int i = 0; i < points; i++) {
                Map.Entry<Vec, Vec> vectors = calculateSpehre(center, radiusX, radiusY, radiusZ, i, phi);

                sendPacketsToSpecificPlayers(vectors.getKey(), vectors.getValue(), players);
            }
        }, TaskSchedule.tick(1), TaskSchedule.tick(1));

        if (!Double.isInfinite(duration)) {
            // Schedule task cancellation
            scheduler.scheduleTask(() -> {
                particleTask.cancel();
                return TaskSchedule.nextTick();
            }, TaskSchedule.seconds((long) duration));
        }

        return particleTask;
    }
}
