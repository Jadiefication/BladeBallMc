package src.main.core.ball.events;

import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;
import net.minestom.server.event.Event;
import net.minestom.server.event.trait.EntityInstanceEvent;
import net.minestom.server.event.trait.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class BallHitEvent implements EntityInstanceEvent, PlayerEvent {

    private final Player player;
    private final Entity entity;

    public BallHitEvent(Player player, Entity entity) {
        this.player = player;
        this.entity = entity;
    }

    @Override
    public @NotNull Player getPlayer() {
        return player;
    }

    public @NotNull Entity getAttackedEntity() {
        return entity;
    }
}
