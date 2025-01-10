package src.main.core.packet;

import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;
import net.minestom.server.event.EventDispatcher;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.trait.EntityInstanceEvent;
import src.main.core.Handler;
import src.main.core.ball.events.BallHitEvent;

public interface PacketHandler extends Handler {

    static void start(GlobalEventHandler handler) {
        handler.addListener(BallHitEvent.class, packet -> {
            Player player = packet.getPlayer();
            Entity entity = packet.getAttackedEntity();

            if (entity != null) {
                BallHitEvent event = new BallHitEvent(player, entity);
                EventDispatcher.call(event);
            }
        });
    }
}
