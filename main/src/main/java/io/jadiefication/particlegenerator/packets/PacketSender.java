package io.jadiefication.particlegenerator.packets;

import net.jadiefication.map.HashMapExtender;
import net.jadiefication.map.MapExtender;
import net.minestom.server.adventure.audience.PacketGroupingAudience;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import net.minestom.server.network.packet.server.ServerPacket;
import net.minestom.server.network.packet.server.play.ParticlePacket;
import net.minestom.server.particle.Particle;
import net.minestom.server.scoreboard.Team;

import java.util.ArrayList;
import java.util.List;

public interface PacketSender {

    static void sendPackets(PacketReceiver receiver, Vec start, Vec end) {
        int particles = 20;
        MapExtender<Particle, List<ParticlePacket>> particlePackets = new HashMapExtender<>();

        // Generate particle packets
        for (int j = 0; j < particles; j++) {
            double progress = (double) j / particles;
            double x = start.x() + (end.x() - start.x()) * progress;
            double z = start.z() + (end.z() - start.z()) * progress;
            double y = start.y();

            for (Particle particleType : receiver.getParticles()) {
                ParticlePacket packet = new ParticlePacket(
                        particleType,
                        false,
                        x, y, z,
                        0f, 0f, 0f,
                        0f,
                        1
                );
                particlePackets.computeIfAbsent(particleType, k -> new ArrayList<>()).add(packet);
            }
        }

        // Send packets based on audience type
        receiver.getAudience().forEach(audienceList -> {
            audienceList.forEach(audience -> {
                if (audience instanceof Instance instance) {
                    instance.getPlayers().forEach(player -> {
                        particlePackets.getValues().forEach(packets ->
                                player.sendPackets(packets.toArray(new ParticlePacket[0]))
                        );
                    });
                } else if (audience instanceof Team team) {
                    team.getPlayers().forEach(player ->
                            particlePackets.getValues().forEach(packets ->
                                    player.sendPackets(packets.toArray(new ParticlePacket[0]))
                            )
                    );
                } else {
                    audience.getPlayers().forEach(player -> {
                        particlePackets.getValues().forEach(packets -> {
                            player.sendPackets(packets.toArray(new ParticlePacket[0]));
                        });
                    });
                }
            });
        });
    }
}
