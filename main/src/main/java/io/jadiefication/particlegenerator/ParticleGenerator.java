package io.jadiefication.particlegenerator;

import io.jadiefication.Nimoh;
import io.jadiefication.particlegenerator.packets.PacketReceiver;
import io.jadiefication.particlegenerator.packets.PacketSender;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.timer.Scheduler;
import net.minestom.server.timer.Task;
import net.minestom.server.timer.TaskSchedule;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class ParticleGenerator {

    private static final Scheduler scheduler = Nimoh.scheduler;
    private static final List<Map.Entry<Vec, Vec>> CACHED_BALL_VECTORS = new ArrayList<>();

    static {
        // Pre-calculate sphere points for 0.5 radius ball
        double phi = Math.PI * (3 - Math.sqrt(5));
        int points = 200;

        for (int i = 0; i < points; i++) {
            Map.Entry<Vec, Vec> vectors = calculateSpehre(new Pos(0, 0, 0), 0.5, 0.5, 0.5, i, phi);
            CACHED_BALL_VECTORS.add(vectors);
        }
    }

    public static Task spawnBladeBall(Pos center, PacketReceiver receiver, double duration) {
        Task particleTask = scheduler.scheduleTask(() -> {
            for (Map.Entry<Vec, Vec> baseVectors : CACHED_BALL_VECTORS) {
                Vec translatedStart = baseVectors.getKey().add(center.x(), center.y(), center.z());
                Vec translatedEnd = baseVectors.getValue().add(center.x(), center.y(), center.z());

                PacketSender.sendPackets(receiver, translatedStart, translatedEnd);
            }
        }, TaskSchedule.tick(1), TaskSchedule.tick(1));

        if (!Double.isInfinite(duration)) {
            scheduler.scheduleTask(() -> {
                particleTask.cancel();
                return TaskSchedule.nextTick();
            }, TaskSchedule.seconds((long) duration));
        }

        return particleTask;
    }

    private static Task createEdges(int edges, Pos center, double radiusX, double radiusY, PacketReceiver receiver, double duration) {

        Vec[] points = new Vec[edges];

        // Each point is 45 degrees (360/8) apart
        for (int i = 0; i < edges; i++) {
            double angle = 2 * Math.PI * i / edges; // 45 degree intervals
            double x = center.x() + radiusX * Math.cos(angle);
            double z = center.z() + radiusY * Math.sin(angle);
            points[i] = new Vec(x, center.y(), z);
        }

        // Connect the 8 points with particles
        Task particleTask = scheduler.scheduleTask(() -> {
            for (int i = 0; i < edges; i++) {
                Vec start = points[i];
                Vec end = points[(i + 1) % edges];

                PacketSender.sendPackets(receiver, start, end);
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

    public static Task spawnOctagonParticles(Pos center, double radiusX, double radiusY, PacketReceiver receiver, double duration) {
        return createEdges(8, center, radiusX, radiusY, receiver, duration);
    }

    public static Task spawnCircleParticles(Pos center, double radiusX, double radiusY, PacketReceiver receiver, double duration) {
        return createEdges(32, center, radiusX, radiusY, receiver, duration);
    }


    public static Task spawnSquareParticles(Pos center, double radiusX, double radiusY, PacketReceiver receiver, double duration) {
        return createEdges(4, center, radiusX, radiusY, receiver, duration);
    }

    public static Task spawnSphereParticles(Pos center, double radiusX, double radiusY, double radiusZ, PacketReceiver receiver, double duration) {
        double phi = Math.PI * (3 - Math.sqrt(5));
        int points = 200; // Adjust for desired density

        Task particleTask = scheduler.scheduleTask(() -> {
            for (int i = 0; i < points; i++) {
                Map.Entry<Vec, Vec> vectors = calculateSpehre(center, radiusX, radiusY, radiusZ, i, phi);

                PacketSender.sendPackets(receiver, vectors.getKey(), vectors.getValue());
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
}
