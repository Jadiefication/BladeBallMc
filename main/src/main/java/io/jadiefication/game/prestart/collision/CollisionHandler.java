package io.jadiefication.game.prestart.collision;

import io.jadiefication.event.PlayerExitEvent;
import io.jadiefication.util.Handler;
import io.jadiefication.game.start.ball.BladeBall;
import net.jadiefication.map.HashMapExtender;
import net.jadiefication.map.MapExtender;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.event.EventDispatcher;
import net.minestom.server.instance.InstanceContainer;

public interface CollisionHandler extends Handler {

    MapExtender<String, CollisionArea> areas = new HashMapExtender<>();
    CollisionHandler handler = new CollisionHandler() {};

    default void defineArea(Vec start, Vec end, String name) {
        areas.put(name, new CollisionArea(start, end));
    }

    @Override
    default void update(InstanceContainer container) {
        container.getPlayers().stream().filter(player -> !BladeBall.isInMatch(player)).forEach(player -> areas.forEach((name, area) -> {
            boolean inArea = area.isInArea(player);
            if (!inArea) {
                if (area.getPlayers().contains(player)) {
                    EventDispatcher.call(new PlayerExitEvent(player, area));
                }
            }
        }));
    }

    @Override
    default void start(InstanceContainer container) {

    }
}
