package io.jadiefication.game.prestart.collision;

import io.jadiefication.customitem.CustomItemHolder;
import io.jadiefication.permission.PermissionablePlayer;
import io.jadiefication.permission.Permissions;
import net.kyori.adventure.text.Component;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.event.player.PlayerBlockInteractEvent;
import net.minestom.server.event.player.PlayerStartDiggingEvent;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import java.util.List;

public abstract class CollisionItem {

    private static final Component name = Component.text("§4§lCollision Wand");
    private static final List<Component> lore = List.of(Component.text("Use this wand to create colliders"));
    private static final Material material = Material.STICK;
    private static Vec start;
    private static Vec end;
    public static final CustomItemHolder item = new CustomItemHolder(ItemStack.of(material),
            name, lore, 0);

    public static void onLeftClick(PlayerStartDiggingEvent event) {
        if (((PermissionablePlayer) event.getPlayer()).hasPermission(Permissions.OP) && event.getPlayer().getItemInMainHand().equals(item.item()
                .withCustomName(item.title())
                .withLore(item.lore())
                .withCustomModelData(item.customModelData()))) {
            Pos pos = Pos.fromPoint(event.getBlockPosition());
            event.getPlayer().sendMessage(Component.text("§d§lFirst position set: " + pos.x() + " " + pos.y() + " " + pos.z()));
            start = pos.asVec();
        }
    }

    public static void onRightClick(PlayerBlockInteractEvent event) {
        if (((PermissionablePlayer) event.getPlayer()).hasPermission(Permissions.OP) && event.getPlayer().getItemInMainHand().equals(item.item()
                .withCustomName(item.title())
                .withLore(item.lore())
                .withCustomModelData(item.customModelData()))) {
            Pos pos = Pos.fromPoint(event.getBlockPosition());
            event.getPlayer().sendMessage(Component.text("§d§lSecond position set: " + pos.x() + " " + pos.y() + " " + pos.z()));
            Vec blockPos = pos.asVec();
            end = blockPos.add(1);
        }
    }

    public static void createArea(String name) {
        CollisionHandler.handler.defineArea(start, end, name);
        start = null;
        end = null;
    }

    public static boolean isSelected() {
        return start != null && end != null;
    }
}
