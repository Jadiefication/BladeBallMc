package src.main.particlegenerator;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.Instance;
import net.minestom.server.network.packet.server.play.ParticlePacket;
import net.minestom.server.particle.Particle;

public class ParticleGenerator {

    private static void sendPackets(Instance instance, Vec start, Vec end) {
        int particles = 20;
        for (int j = 0; j < particles; j++) {
            double progress = (double) j / particles;
            double x = start.x() + (end.x() - start.x()) * progress;
            double z = start.z() + (end.z() - start.z()) * progress;
            double y = start.y();

            instance.sendGroupedPacket(new ParticlePacket(
                    Particle.END_ROD,
                    false,
                    x, y, z,
                    0f, 0f, 0f,
                    0f,
                    1
            ));
        }
    }

    public static void spawnOctagonParticles(Instance instance, Pos center, double radiusX, double radiusY) {
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
        for (int i = 0; i < 8; i++) {
            Vec start = points[i];
            Vec end = points[(i + 1) % 8];

            sendPackets(instance, start, end);
        }
    }

    public static void spawnSquareParticles(Instance instance, Pos center, double radiusX, double radiusY) {
        Vec[] points = new Vec[4];

        for (int i = 0; i < 4; i++) {
            double angle = Math.PI * i / 2;

            double x = center.x() + radiusX * Math.cos(angle);
            double z = center.z() + radiusY * Math.sin(angle);

            points[i] = new Vec(x, center.y(), z);
        }

        for (int i = 0; i < 4; i++) {
            Vec start = points[i];
            Vec end = points[(i+1) % 4];

            sendPackets(instance, start, end);
        }
    }

}
