package io.jadiefication.util.game.prestart.collision;

import io.jadiefication.Nimoh;
import io.jadiefication.event.PlayerCollideEvent;
import io.jadiefication.particlegenerator.packets.PacketReceiver;
import io.jadiefication.particlegenerator.packets.PacketSender;
import io.jadiefication.permission.PermissionablePlayer;
import net.jadiefication.map.HashMapExtender;
import net.jadiefication.map.MapExtender;
import net.kyori.adventure.text.Component;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.metadata.display.TextDisplayMeta;
import net.minestom.server.event.EventDispatcher;
import net.minestom.server.particle.Particle;
import net.minestom.server.timer.Task;
import net.minestom.server.timer.TaskSchedule;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CollisionArea {

    private final Vec min;
    private final Vec max;
    private final Set<Player> players = new HashSet<>();
    private final Vec center;
    private Task cubeTask;
    private final Entity text = new Entity(EntityType.TEXT_DISPLAY);

    public CollisionArea(Vec start, Vec end) {
        this.min = new Vec(
                Math.min(start.x(), end.x()),
                Math.min(start.y(), end.y()),
                Math.min(start.z(), end.z())
        );
        this.max = new Vec(
                Math.max(start.x(), end.x()),
                Math.max(start.y(), end.y()),
                Math.max(start.z(), end.z())
        );
        center = new Vec(
                (min.x() + max.x()) / 2,
                (min.y() + max.y()) / 2,
                (min.z() + max.z()) / 2
        );
        text.setInvisible(true);
        text.setInstance(Nimoh.instanceContainer);
        text.teleport(center.asPosition());
        TextDisplayMeta meta = ((TextDisplayMeta) text.getEntityMeta());
        meta.setText(Component.text(CollisionHandler.areas.getKey(this)));
        meta.setAlignment(TextDisplayMeta.Alignment.CENTER);
        meta.setSeeThrough(true);
        meta.setShadow(true);
    }

    public boolean isInArea(Player player) {
        Vec pos = Vec.fromPoint(player.getPosition());
        if (pos.x() >= min.x() && pos.x() <= max.x()
                && pos.y() >= min.y() && pos.y() <= max.y()
                && pos.z() >= min.z() && pos.z() <= max.z()) {
            this.players.add(player);
            EventDispatcher.call(new PlayerCollideEvent(player, this));
            return true;
        } else {
            return false;
        }
    }

    private void sendCubePackets(PacketReceiver receiver, Vec min, Vec max) {
        // Draw all 12 edges of the cube
        // Bottom square
        PacketSender.sendPackets(receiver, new Vec(min.x(), min.y(), min.z()), new Vec(max.x(), min.y(), min.z()));
        PacketSender.sendPackets(receiver, new Vec(max.x(), min.y(), min.z()), new Vec(max.x(), min.y(), max.z()));
        PacketSender.sendPackets(receiver, new Vec(max.x(), min.y(), max.z()), new Vec(min.x(), min.y(), max.z()));
        PacketSender.sendPackets(receiver, new Vec(min.x(), min.y(), max.z()), new Vec(min.x(), min.y(), min.z()));

        // Top square
        PacketSender.sendPackets(receiver, new Vec(min.x(), max.y(), min.z()), new Vec(max.x(), max.y(), min.z()));
        PacketSender.sendPackets(receiver, new Vec(max.x(), max.y(), min.z()), new Vec(max.x(), max.y(), max.z()));
        PacketSender.sendPackets(receiver, new Vec(max.x(), max.y(), max.z()), new Vec(min.x(), max.y(), max.z()));
        PacketSender.sendPackets(receiver, new Vec(min.x(), max.y(), max.z()), new Vec(min.x(), max.y(), min.z()));

        // Vertical edges
        PacketSender.sendPackets(receiver, new Vec(min.x(), min.y(), min.z()), new Vec(min.x(), max.y(), min.z()));
        PacketSender.sendPackets(receiver, new Vec(max.x(), min.y(), min.z()), new Vec(max.x(), max.y(), min.z()));
        PacketSender.sendPackets(receiver, new Vec(max.x(), min.y(), max.z()), new Vec(max.x(), max.y(), max.z()));
        PacketSender.sendPackets(receiver, new Vec(min.x(), min.y(), max.z()), new Vec(min.x(), max.y(), max.z()));
    }

    public void show(Player player) {
        text.setInvisible(false);
        MapExtender<Particle, List<Player>> receiver = new HashMapExtender<>();
        receiver.put(Particle.WHITE_ASH, List.of(player));
        cubeTask = Nimoh.scheduler.scheduleTask(() -> {
            sendCubePackets(new PacketReceiver(receiver), this.min, this.max);
        }, TaskSchedule.tick(1), TaskSchedule.tick(1));
    }

    public void hide(Player player) {
        text.setInvisible(true);
        cubeTask.cancel();
    }

    public void removePlayer(Player player) {
        this.players.remove(player);
    }

    public Set<Player> getPlayers() {
        return this.players;
    }
}
