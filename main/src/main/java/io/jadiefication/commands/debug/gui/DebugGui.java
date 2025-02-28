package io.jadiefication.commands.debug.gui;

import io.jadiefication.util.game.prestart.collision.CollisionItem;
import io.jadiefication.util.gui.Border;
import io.jadiefication.util.gui.Heads;
import io.jadiefication.customitem.CustomItem;
import io.jadiefication.permission.PermissionablePlayer;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.inventory.InventoryPreClickEvent;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.monitoring.BenchmarkManager;
import net.minestom.server.timer.SchedulerManager;
import net.minestom.server.timer.Task;
import net.minestom.server.timer.TaskSchedule;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class DebugGui extends Inventory {

    // Store tasks and BossBars per player
    private static final Map<UUID, Task> activeTasks = new HashMap<>();
    private static final Map<UUID, BossBar> activeBossBars = new HashMap<>();
    public static final AtomicInteger j = new AtomicInteger();
    public static final ItemStack collider = CollisionItem.item.item()
            .withCustomName(CollisionItem.item.title())
            .withLore(CollisionItem.item.lore())
            .withCustomModelData(CollisionItem.item.customModelData());

    public static ItemStack createPerformanceCheckerItem() {
        return CustomItem.registerItem(
                Component.text("§4§lPerformance Checker"),
                List.of(),
                Heads.createHead(Heads.createUrl("http://textures.minecraft.net/texture/7116e6ae1ea274da5668d8e7efecd91fe5453b9d3774aaf84d9a41186c0f1e7a")),
                2,
                event -> handlePerformanceCheckerClick((InventoryPreClickEvent) event)
        );
    }

    private static void handlePerformanceCheckerClick(InventoryPreClickEvent event) {
        PermissionablePlayer player = (PermissionablePlayer) event.getPlayer();
        UUID playerUUID = player.getUuid();
        SchedulerManager schedulerManager = MinecraftServer.getSchedulerManager();
        BenchmarkManager benchmarkManager = MinecraftServer.getBenchmarkManager();

        if (event.getClickedItem().equals(createPerformanceCheckerItem())) {
            if (activeTasks.containsKey(playerUUID)) {
                // Stop the task and hide the BossBar
                Task task = activeTasks.remove(playerUUID);
                task.cancel();

                BossBar bossBar = activeBossBars.remove(playerUUID);
                player.hideBossBar(bossBar);

                player.sendMessage(Component.text("§cPerformance Checker stopped."));
            } else {
                // Create and show the BossBar
                BossBar bossBar = BossBar.bossBar(
                        Component.text("§4§lRAM: §r0 MB"),
                        1.0f,
                        BossBar.Color.RED,
                        BossBar.Overlay.PROGRESS
                );
                player.showBossBar(bossBar);
                activeBossBars.put(playerUUID, bossBar);

                // Start the task to update the BossBar
                Task task = schedulerManager.buildTask(() -> {
                    long ramUsage = benchmarkManager.getUsedMemory() / 1024 / 1024;
                    bossBar.name(Component.text("§4§lRAM: §r" + ramUsage + " MB"));
                }).repeat(TaskSchedule.seconds(1)).schedule();

                activeTasks.put(playerUUID, task);
                player.sendMessage(Component.text("§aPerformance Checker started."));
            }
            event.setCancelled(true);
        }
    }

    public DebugGui() {
        super(InventoryType.CHEST_6_ROW, Component.text("Debug Inventory"));
        List<Material> items = List.of(Material.BARRIER, Material.COMMAND_BLOCK, Material.STRUCTURE_VOID, Material.LIGHT, Material.REPEATING_COMMAND_BLOCK);
        Border.setInventoryBorder(this);
        setItemStack(40, createPerformanceCheckerItem());
        setItemStack(41, collider);
        for (int i = 0; i < items.size(); i++) {
            setItemStack(i + 10, ItemStack.of(items.get(i)));
        }

    }
}
