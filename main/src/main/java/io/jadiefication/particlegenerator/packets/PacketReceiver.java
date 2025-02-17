package io.jadiefication.particlegenerator.packets;

import net.jadiefication.map.HashMapExtender;
import net.jadiefication.map.MapExtender;
import net.minestom.server.adventure.audience.PacketGroupingAudience;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import net.minestom.server.particle.Particle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PacketReceiver {
    private final MapExtender<Particle, List<? extends PacketGroupingAudience>> players = new HashMapExtender<>();

    public PacketReceiver(Instance instance, Particle particle) {
        this.players.put(particle, List.of(instance));
    }

    public PacketReceiver(MapExtender<Particle, List<Player>> players) {
        Map<Particle, List<PacketGroupingAudience>> audience = new HashMap<>();

        players.forEach((particle, playerL) -> {
            List<PacketGroupingAudience> groupAudience = List.of(PacketGroupingAudience.of(playerL));
            this.players.put(particle, groupAudience);
        });
    }

    public PacketReceiver(Map<Particle, List<? extends PacketGroupingAudience>> players) {
        this.players.putAll(players);
    }

    public List<List<? extends PacketGroupingAudience>> getAudience() {
        return new ArrayList<>(this.players.getValues());
    }

    public List<Particle> getParticles() {
        return new ArrayList<>(this.players.getKeys());
    }

    public void addAudience(Particle particle, PacketGroupingAudience audience) {
        List<PacketGroupingAudience> audiences = new ArrayList<>();
        audiences.add(audience);
        this.players.put(particle, audiences);
    }

    public void removeAudience(Particle particle, PacketGroupingAudience audience) {
        if (this.players.containsKey(particle)) {
            List<PacketGroupingAudience> currentAudiences = new ArrayList<>(
                    this.players.get(particle)
            );
            currentAudiences.remove(audience);

            if (currentAudiences.isEmpty()) {
                this.players.remove(particle);
            } else {
                this.players.put(particle, currentAudiences);
            }
        }
    }
}

