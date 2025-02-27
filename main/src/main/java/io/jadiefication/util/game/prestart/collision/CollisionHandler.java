package io.jadiefication.util.game.prestart.collision;

import io.jadiefication.customitem.CustomItem;
import io.jadiefication.event.PlayerExitEvent;
import io.jadiefication.util.Handler;
import io.jadiefication.util.game.start.ball.BladeBall;
import net.jadiefication.map.HashMapExtender;
import net.jadiefication.map.MapExtender;
import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.event.EventDispatcher;
import net.minestom.server.event.player.PlayerBlockInteractEvent;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
