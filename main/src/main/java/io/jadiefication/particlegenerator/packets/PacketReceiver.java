package io.jadiefication.particlegenerator.packets;

import net.jadiefication.map.HashMapExtender;
import net.jadiefication.map.MapExtender;
import net.minestom.server.adventure.audience.PacketGroupingAudience;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import net.minestom.server.particle.Particle;
import net.minestom.server.scoreboard.Team;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PacketReceiver {

    public final MapExtender<Particle, List<? extends PacketGroupingAudience>> players = new HashMapExtender<>();

    public PacketReceiver(Instance instance, Particle particle) {
        this.players.put(particle,
                (List<? extends PacketGroupingAudience>) instance.getPlayers().stream().toList()
        );
    }

    public PacketReceiver(MapExtender<Particle, List<PacketGroupingAudience>> players) {
        this.players.putAll(players);
    }

    public List<List<? extends PacketGroupingAudience>> getAudience() {
        return this.players.getValues();
    }

    public List<Particle> getParticles() {
        return this.players.getKeys();
    }
}
