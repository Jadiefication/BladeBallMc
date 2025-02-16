package io.jadiefication.particlegenerator.packets;

import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import net.minestom.server.network.packet.server.play.ParticlePacket;
import net.minestom.server.particle.Particle;
import net.minestom.server.scoreboard.Team;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface PacketSender {

    static void sendPacketsToSpecificPlayers(Vec start, Vec end, Map<Particle, List<Player>> players) {
        int particles = 20;
        Map<Particle, List<ParticlePacket>> particlePackets = new HashMap<>();
        for (int j = 0; j < particles; j++) {
            double progress = (double) j / particles;
            double x = start.x() + (end.x() - start.x()) * progress;
            double z = start.z() + (end.z() - start.z()) * progress;
            double y = start.y();

            for (Map.Entry<Particle, List<Player>> entry : players.entrySet()) {
                Particle particleType = entry.getKey(); // Particle type (e.g., FLAME, SMOKE)

                // Create the particle packet
                ParticlePacket packet = new ParticlePacket(
                        particleType,
                        false,  // Long distance sending (optional, usually false for short visibility effects)
                        x, y, z, // Particle position
                        0f, 0f, 0f, // No velocity, particles remain static
                        0f,     // Extra data; typically unused here
                        1       // Particle count, set to 1 per packet
                );

                // Add this packet to the corresponding particle list
                particlePackets.computeIfAbsent(particleType, k -> new ArrayList<>()).add(packet);
            }


        }

        players.forEach((key, value) -> {
            value.forEach(player -> {
                particlePackets.get(key).forEach(player::sendPacket);
            });
        });
    }

    static void sendPacketsToSpecificTeams(Vec start, Vec end, Map<Particle, Team> players) {
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

    static void sendPackets(Instance instance, Vec start, Vec end, Particle particle) {
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
}
