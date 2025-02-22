package io.jadiefication.eventfunctions;

import io.jadiefication.AbilitiesHolder;
import io.jadiefication.Nimoh;
import io.jadiefication.Server;
import io.jadiefication.commands.debug.gui.DebugGui;
import io.jadiefication.core.ball.BallHandler;
import io.jadiefication.core.ball.BladeBall;
import io.jadiefication.core.data.player.PlayerDataHandler;
import io.jadiefication.core.game.prestart.gui.AbilitySelectionMenu;
import io.jadiefication.core.gui.Border;
import io.jadiefication.core.item.SwordItems;
import io.jadiefication.customitem.CustomItem;
import io.jadiefication.customitem.CustomItemHolder;
import io.jadiefication.permission.Permission;
import io.jadiefication.permission.PermissionablePlayer;
import net.kyori.adventure.text.Component;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.event.EventDispatcher;
import net.minestom.server.event.inventory.InventoryCloseEvent;
import net.minestom.server.event.inventory.InventoryOpenEvent;
import net.minestom.server.event.inventory.InventoryPreClickEvent;
import net.minestom.server.event.item.PlayerBeginItemUseEvent;
import net.minestom.server.event.player.*;
import net.minestom.server.event.server.ServerListPingEvent;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockFace;
import net.minestom.server.inventory.AbstractInventory;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.network.packet.server.play.SetCooldownPacket;
import net.minestom.server.timer.TaskSchedule;

import java.util.List;
import java.util.Objects;

public abstract class EventFunction implements PlayerDataHandler {

    public static void onBreak(PlayerBlockBreakEvent event) {
        ItemStack item = ItemStack.builder(Objects.requireNonNull(event.getBlock().registry().material())).build();
        PermissionablePlayer player = (PermissionablePlayer) event.getPlayer();
        if (player.hasPermission(Permission.BREAK) && !player.getGameMode().equals(GameMode.ADVENTURE)) {
            if (player.getGameMode().equals(GameMode.CREATIVE)) return;
            player.getInventory().addItemStack(item);
        } else {
            event.setCancelled(true);
        }
    }

    public static void onJoin(AsyncPlayerConfigurationEvent event) {
        final PermissionablePlayer player = (PermissionablePlayer) event.getPlayer();

        // Set the spawning instance immediately in the main thread to avoid NullPointerException
        event.setSpawningInstance(Nimoh.instanceContainer);

        // Use a thread pool for better management of asynchronous tasks
        Nimoh.executorService.submit(() -> {
            synchronized (player) {
                // Perform asynchronous tasks like sending pack info and getting player data
                Server.sendPackInfo(player);
                PlayerDataHandler.getData(player);
            }

            // Perform other logic after player data is loaded
            if (player.getName().equals(Component.text("Jadiefication"))) {
                player.setPermissionLevel(4);
            }

            // Set the respawn point after all other tasks are completed
            player.setRespawnPoint(new Pos(0.0, 46.0, 0.0));
        });
    }

    public static void onWorldJoin(PlayerSpawnEvent event) {
        event.getPlayer().openInventory(new AbilitySelectionMenu());
    }

    public static void onLeave(PlayerDisconnectEvent event) {
        final Player player = event.getPlayer();
        if (BladeBall.isHomedUponPlayer(player)) Nimoh.game.hasPlayer = false;
        AbilitiesHolder.cooldownMap.remove(player.getUuid());
        BladeBall.shieldCooldown.remove(player);
        PlayerDataHandler.updateData(player);
    }


    public static void onPlace(PlayerBlockPlaceEvent event) {

        List<Block> axisList = List.of(Block.OAK_LOG, Block.ACACIA_LOG, Block.BIRCH_LOG, Block.DARK_OAK_LOG, Block.JUNGLE_LOG, Block.MANGROVE_LOG, Block.SPRUCE_LOG, Block.PALE_OAK_LOG,
                Block.STRIPPED_OAK_LOG, Block.STRIPPED_ACACIA_LOG, Block.STRIPPED_BIRCH_LOG, Block.STRIPPED_DARK_OAK_LOG, Block.STRIPPED_JUNGLE_LOG, Block.STRIPPED_MANGROVE_LOG, Block.STRIPPED_SPRUCE_LOG, Block.STRIPPED_PALE_OAK_LOG,
                Block.QUARTZ_PILLAR, Block.PURPUR_PILLAR, Block.HAY_BLOCK, Block.BONE_BLOCK, Block.BAMBOO_BLOCK,
                        Block.BASALT, Block.POLISHED_BASALT, Block.CRYING_OBSIDIAN, Block.CHAIN);
        List<Block> rotationList = List.of(Block.OAK_SIGN, Block.SPRUCE_SIGN, Block.BIRCH_SIGN, Block.JUNGLE_SIGN, Block.ACACIA_SIGN, Block.DARK_OAK_SIGN, Block.MANGROVE_SIGN, Block.BAMBOO_SIGN, Block.CRIMSON_SIGN, Block.WARPED_SIGN,
                Block.CREEPER_HEAD, Block.DRAGON_HEAD, Block.PIGLIN_HEAD, Block.PLAYER_HEAD, Block.ZOMBIE_HEAD, Block.WITHER_SKELETON_SKULL, Block.SKELETON_SKULL,
                Block.WHITE_BANNER, Block.ORANGE_BANNER, Block.MAGENTA_BANNER, Block.LIGHT_BLUE_BANNER, Block.YELLOW_BANNER, Block.LIME_BANNER, Block.PINK_BANNER, Block.GRAY_BANNER, Block.LIGHT_GRAY_BANNER, Block.CYAN_BANNER, Block.PURPLE_BANNER, Block.BLUE_BANNER, Block.BROWN_BANNER, Block.GREEN_BANNER, Block.RED_BANNER, Block.BLACK_BANNER);
        List<Block> facingList = List.of(
                Block.FURNACE, Block.DISPENSER, Block.DROPPER, Block.OBSERVER, Block.PISTON, Block.STICKY_PISTON,
                Block.HOPPER, Block.LANTERN, Block.WALL_TORCH, Block.LEVER, Block.REDSTONE_TORCH, Block.END_ROD,
                Block.BELL, Block.GRINDSTONE, Block.LOOM, Block.STONECUTTER
        );


        PermissionablePlayer player = (PermissionablePlayer) event.getPlayer();
        Block block = event.getBlock();
        BlockFace face = event.getBlockFace();
        Vec direction = player.getPosition().direction();

        String axis;
        double absX = Math.abs(direction.x());
        double absY = Math.abs(direction.y());
        double absZ = Math.abs(direction.z());

        if (absY > absX && absY > absZ) {
            axis = "y";
        } else if (absX > absZ) {
            axis = "x";
        } else {
            axis = "z";
        }

        if (player.hasPermission(Permission.PLACE) && !player.getGameMode().equals(GameMode.ADVENTURE) && CustomItemHolder.hasItem(player.getItemInMainHand()).isEmpty()) {
            Block directedBlock;
            if (axisList.contains(block)) {
                directedBlock = block.withProperty("axis", axis);
            } else if (rotationList.contains(block)) {
                int rotation = (int) ((Math.atan2(direction.z(), direction.x()) * 16.0 / (2 * Math.PI) + 16.5) % 16);
                directedBlock = block.withProperty("rotation", String.valueOf(rotation));
            } else if (facingList.contains(block)) {
                directedBlock = block.withProperty("facing", face.name().toLowerCase());
            } else {
                directedBlock = block;
            }
            event.setBlock(directedBlock);
        } else if (CustomItemHolder.hasItem(player.getItemInMainHand()).isPresent()) {
            event.setCancelled(true);
            if (AbilitiesHolder.isAbility(player.getItemInMainHand())) {
                PlayerUseItemEvent e = new PlayerUseItemEvent(player, Objects.requireNonNull(player.getItemUseHand()), player.getItemInMainHand(), 1);
                EventDispatcher.call(e);
            }
        } else {
            event.setCancelled(true);
        }
    }

    public static void onPing(ServerListPingEvent event) {
        event.getResponseData().setMaxPlayer(20);
    }

    public static void onItemUse(PlayerUseItemEvent event) {
        ItemStack item = event.getItemStack();
        if (CustomItem.getItems().contains(item) && ((PermissionablePlayer) event.getPlayer()).hasPermission(Permission.USE_CUSTOM_ITEM)) {
            CustomItem.getItemFunctionality(item).accept(event);
        }
    }

    public static void onInventoryClick(InventoryPreClickEvent event) {
        AbstractInventory inventory = event.getInventory();
        Player player = event.getPlayer();

        if (inventory instanceof DebugGui) {
            ItemStack item = event.getClickedItem();
            if (item.equals(Border.border)) {
                event.setCancelled(true);
            } else if (item.equals(DebugGui.createPerformanceCheckerItem())) {
                CustomItem.getItemFunctionality(DebugGui.createPerformanceCheckerItem()).accept(event);
            } else if (item.equals(ItemStack.builder(Material.REPEATING_COMMAND_BLOCK).build())) {
                event.setCancelled(true);
                player.sendMessage(Component.text("Testing enabled"));
                Nimoh.testing = true;
            }
        }
        if (inventory instanceof AbilitySelectionMenu) {
            ItemStack item = event.getClickedItem();
            if (AbilitiesHolder.isAbility(item)) {
                AbilitySelectionMenu.hasAbility = true;
                player.getInventory().setItemStack(1, Objects.requireNonNull(AbilitiesHolder.getAbility(item)));
            }
            if (item.equals(Border.border)) {
                return;
            } else if (item.equals(SwordItems.shield)) {
                player.getInventory().setItemStack(0, SwordItems.shield);
                AbilitySelectionMenu.hasSword = true;
            }
            event.setCancelled(true);
        }
    }

    public static void onInventoryClose(InventoryCloseEvent event) {
        AbstractInventory inventory = event.getInventory();
        if (inventory instanceof AbilitySelectionMenu) {
            if (!AbilitySelectionMenu.hasSword || !AbilitySelectionMenu.hasAbility) {
                event.setNewInventory(new AbilitySelectionMenu());
            }
        }
    }


    public static void onInventoryOpen(InventoryOpenEvent event) {
        if (event.getInventory() instanceof DebugGui inventory) {
            DebugGui.j.set(0);
            if (BallHandler.BallState.task != null) {
                DebugGui.j.getAndIncrement();
                inventory.setItemStack(14, ItemStack.builder(Material.DEBUG_STICK)
                        .customName(Component.text(DebugGui.j.get() + " Particle Tasks"))
                        .build());
            }
        }
    }

    public static void onItemBlock(PlayerBeginItemUseEvent event) {
        Player player = event.getPlayer();
        // Check if the item is a shield by comparing its material.
        if (event.getItemStack().material() == Material.SHIELD) {
            // If on cooldown, cancel the event.
            if (BladeBall.shieldCooldown.containsKey(player)) {
                event.setCancelled(true);
                return;
            }

            if (BladeBall.isHomedUponPlayer(player) && BallHandler.BallState.ballPosition.distanceSquared(player.getPosition()) < 0.25) {
                Nimoh.game.setPlayerAttached(false);
                BallHandler.BallState.firstTarget = false;
                BallHandler.BallState.playerWhomHitTheBall = player;
                Nimoh.game.multipleSpeed(0.1);
            }

            // Immediately send a cooldown packet so the client shows the cooldown.
            player.sendPacket(new SetCooldownPacket(String.valueOf(Material.SHIELD.id()), 40));
            // Mark the player as on cooldown.
            BladeBall.shieldCooldown.put(player, 2);

            // Optionally, restore the shield after the cooldown has ended.
            Nimoh.scheduler.buildTask(() -> {
                // Store the shield item and remove it to force cancel usage.
                ItemStack shieldItem = event.getItemStack();
                player.setItemInMainHand(ItemStack.AIR);
                Nimoh.scheduler.buildTask(() -> {
                    // Store the shield item and remove it to force cancel usage.
                    player.setItemInMainHand(shieldItem);
                }).delay(TaskSchedule.tick(5)).schedule();
            }).delay(TaskSchedule.tick(30)).schedule();
        }
    }


}
