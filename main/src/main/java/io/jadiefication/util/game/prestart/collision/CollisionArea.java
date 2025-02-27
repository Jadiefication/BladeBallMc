package io.jadiefication.util.game.prestart.collision;

import io.jadiefication.event.PlayerCollideEvent;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Player;
import net.minestom.server.event.EventDispatcher;

import java.util.HashSet;
import java.util.Set;

public class CollisionArea {

    private final Vec min;
    private final Vec max;
    private final Set<Player> players = new HashSet<>();

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

    public void removePlayer(Player player) {
        this.players.remove(player);
    }

    public Set<Player> getPlayers() {
        return this.players;
    }
}
