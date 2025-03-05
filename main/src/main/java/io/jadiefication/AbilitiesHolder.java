package io.jadiefication;

import io.jadiefication.customitem.CustomItem;
import io.jadiefication.customitem.CustomItemHolder;
import net.kyori.adventure.text.Component;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.PlayerUseItemEvent;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.timer.Task;
import net.minestom.server.timer.TaskSchedule;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public interface AbilitiesHolder {

    Map<UUID, Long> cooldownMap = new HashMap<>();
    long cooldown = Long.parseLong(Nimoh.config.split("cooldown=")[1].split("\n")[0]);
    Set<Player> playersOnPlatform = new HashSet<>();

    ItemStack dash = CustomItem.registerItem(Component.text("§8§lDash ability"), List.of(Component.text("Use this ability to dash forward")),
            Material.BLACK_STAINED_GLASS_PANE, 1, event -> {
                PlayerUseItemEvent e = (PlayerUseItemEvent) event;
                final Player player = e.getPlayer();
                if (!cooldownMap.containsKey(player.getUuid())) {
                    Pos pos = player.getPosition();
                    Vec direction = pos.direction().normalize();
                    Vec movement = direction.mul(48);

                    Pos placeWhereWeWillGo = pos.add(movement);
                    player.setVelocity(movement);

                    // Start a task to monitor the player's position
                    Task task = Nimoh.scheduler.scheduleTask(() -> {
                        // Check if the player is at or above the target height
                        if (player.getPosition().equals(placeWhereWeWillGo)) {
                            player.setVelocity(new Vec(0, 0, 0)); // Stop the upward motion
                        }
                    }, TaskSchedule.millis(10), TaskSchedule.millis(10)); // Tasks repeat regularly

                    cooldownMap.put(player.getUuid(), cooldown);
                }
    });

    ItemStack superJump = CustomItem.registerItem(Component.text("§8§lSuper jump ability"), List.of(Component.text("Use this ability to jump super high")),
            Material.BLACK_STAINED_GLASS_PANE, 2, event -> {
                PlayerUseItemEvent e = ((PlayerUseItemEvent) event);
                final Player player = e.getPlayer();
                if (!cooldownMap.containsKey(player.getUuid())) {
                    double initialY = player.getPosition().y();

                    Vec upwardVelocity = new Vec(0, 24, 0); // Adjust as needed
                    player.setVelocity(upwardVelocity);

                    // Start a task to monitor the player's position
                    Task task = Nimoh.scheduler.scheduleTask(() -> {
                        // Check if the player is at or above the target height
                        if (player.getPosition().y() >= initialY + 24) {
                            player.setVelocity(new Vec(0, 0, 0)); // Stop the upward motion
                        }
                    }, TaskSchedule.millis(10), TaskSchedule.millis(10)); // Tasks repeat regularly

                    cooldownMap.put(player.getUuid(), cooldown);
                }
    });

    ItemStack platform = CustomItem.registerItem(Component.text("§8§lPlatform ability"), List.of(Component.text("Use this ability to raise a platform below you"), Component.text("You cannot move from the platform")),
            Material.BLACK_STAINED_GLASS_PANE, 3, event -> {
                PlayerUseItemEvent e = ((PlayerUseItemEvent) event);
                final Player player = e.getPlayer();
                if (!cooldownMap.containsKey(player.getUuid())) {
                    AtomicReference<Task> taskRevertReference = new AtomicReference<>();
                    AtomicReference<Task> taskReference = new AtomicReference<>();
                    AtomicInteger heightCounter = new AtomicInteger(1); // Tracks the height built

                    Pos initialPosition = player.getPosition(); // Get the player's starting position
                    playersOnPlatform.add(player);

                    Task task = Nimoh.scheduler.scheduleTask(() -> {
                        // Build the current platform below the player
                        Pos platformPos = initialPosition.add(0, heightCounter.get(), 0); // Use fixed initial Y position
                        Nimoh.instanceContainer.setBlock(platformPos.sub(0, 1, 0), Block.OAK_PLANKS);

                        // Increment height counter and teleport the player
                        heightCounter.getAndIncrement();
                        player.teleport(initialPosition.add(0, heightCounter.get(), 0)); // Add next height level

                        // Stop the task once heightCounter == 3 (builds 3 blocks total)
                        if (heightCounter.get() >= 4) {
                            Task thisTask = taskReference.get();
                            if (thisTask != null) {
                                thisTask.cancel();
                            }
                        }
                    }, TaskSchedule.millis(10), TaskSchedule.millis(10));


                    Task revertTask = Nimoh.scheduler.scheduleTask(() -> {
                        for (int j = 1; j < 4; j++) {
                            Nimoh.instanceContainer.setBlock(player.getPosition().sub(0, j, 0), Block.AIR);
                        }
                        Task thisTask = taskRevertReference.get();
                        if (thisTask != null) {
                            thisTask.cancel();
                            playersOnPlatform.remove(player);
                        }
                        return TaskSchedule.millis(10);
                    }, TaskSchedule.seconds(5));

                    taskReference.set(task);
                    taskRevertReference.set(revertTask);

                    cooldownMap.put(player.getUuid(), cooldown);
                }
            });

    static boolean isAbility(ItemStack item) {
        return item == dash || item == superJump || item == platform;
    }

    static ItemStack getAbility(ItemStack item) {
        if (!isAbility(item)) return null;
        else {
            CustomItemHolder abilityHolder = CustomItemHolder.hasItem(item).get();
            return abilityHolder.item().withCustomName(abilityHolder.title())
                    .withLore(abilityHolder.lore())
                    .withCustomModelData(abilityHolder.customModelData());
        }
    }
}
