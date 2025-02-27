package io.jadiefication.event;

import io.jadiefication.util.game.prestart.collision.CollisionArea;
import net.minestom.server.entity.Player;
import net.minestom.server.event.trait.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerExitEvent implements PlayerEvent {

    private final Player player;
    private final CollisionArea area;

    public PlayerExitEvent(Player player, CollisionArea area) {
        this.player = player;
        this.area = area;
        area.removePlayer(player);
    }

    @Override
    public @NotNull Player getPlayer() {
        return this.player;
    }

    public @NotNull CollisionArea getArea() {
        return this.area;
    }
}
