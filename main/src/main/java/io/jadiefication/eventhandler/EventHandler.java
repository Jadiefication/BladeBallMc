package io.jadiefication.eventhandler;

import io.jadiefication.AbilitiesHolder;
import io.jadiefication.Nimoh;
import io.jadiefication.Server;
import io.jadiefication.commands.debug.gui.DebugGui;
import io.jadiefication.eventhandler.block.BlockFunctions;
import io.jadiefication.util.game.prestart.collision.CollisionItem;
import io.jadiefication.util.game.start.ball.BallHandler;
import io.jadiefication.util.game.start.ball.BladeBall;
import io.jadiefication.util.data.player.PlayerDataHandler;
import io.jadiefication.util.game.prestart.gui.AbilitySelectionMenu;
import io.jadiefication.util.gui.Border;
import io.jadiefication.util.data.game.item.SwordItems;
import io.jadiefication.customitem.CustomItem;
import io.jadiefication.customitem.CustomItemHolder;
import io.jadiefication.permission.PermissionablePlayer;
import io.jadiefication.permission.Permissions;
import io.jadiefication.permission.sql.PermissionSQLHandler;
import net.jadiefication.map.HashMapExtender;
import net.jadiefication.map.MapExtender;
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
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockFace;
import net.minestom.server.inventory.AbstractInventory;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.network.packet.server.play.SetCooldownPacket;
import net.minestom.server.timer.TaskSchedule;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class EventHandler implements PlayerDataHandler {

    public static void onBreak(PlayerBlockBreakEvent event) {
        ItemStack item = ItemStack.builder(Objects.requireNonNull(event.getBlock().registry().material())).build();
        PermissionablePlayer player = (PermissionablePlayer) event.getPlayer();
        if (player.getItemInMainHand().equals(CollisionItem.item.item()
                .withCustomName(CollisionItem.item.title())
                .withLore(CollisionItem.item.lore())
                .withCustomModelData(CollisionItem.item.customModelData()))) {
            event.setCancelled(true);
            EventDispatcher.call(new PlayerStartDiggingEvent(player, event.getBlock(), event.getBlockPosition(), event.getBlockFace()));
            return; // Allow the CollisionItem handlers to process it
        }
        if (player.hasPermission(Permissions.BREAK) && !player.getGameMode().equals(GameMode.ADVENTURE)) {
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
                PlayerDataHandler.getData(player);
                PermissionSQLHandler.getPermissions(player);
                Server.sendPackInfo(player);
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
        List<Block> fenceList = List.of(
                Block.OAK_FENCE, Block.SPRUCE_FENCE, Block.BIRCH_FENCE, Block.JUNGLE_FENCE,
                Block.ACACIA_FENCE, Block.DARK_OAK_FENCE, Block.MANGROVE_FENCE, Block.BAMBOO_FENCE,
                Block.CRIMSON_FENCE, Block.WARPED_FENCE
        );
        List<Block> slabList = List.of(
                Block.OAK_SLAB, Block.SPRUCE_SLAB, Block.BIRCH_SLAB, Block.JUNGLE_SLAB,
                Block.ACACIA_SLAB, Block.DARK_OAK_SLAB, Block.MANGROVE_SLAB, Block.BAMBOO_SLAB,
                Block.STONE_SLAB, Block.SMOOTH_STONE_SLAB, Block.SANDSTONE_SLAB, Block.COBBLESTONE_SLAB,
                Block.BRICK_SLAB, Block.NETHER_BRICK_SLAB, Block.QUARTZ_SLAB, Block.RED_SANDSTONE_SLAB,
                Block.PURPUR_SLAB, Block.BLACKSTONE_SLAB, Block.POLISHED_BLACKSTONE_SLAB, Block.WARPED_SLAB, Block.CRIMSON_SLAB
        );
        List<Block> stairList = List.of(
                Block.OAK_STAIRS, Block.SPRUCE_STAIRS, Block.BIRCH_STAIRS, Block.JUNGLE_STAIRS,
                Block.ACACIA_STAIRS, Block.DARK_OAK_STAIRS, Block.MANGROVE_STAIRS, Block.BAMBOO_STAIRS,
                Block.STONE_STAIRS, Block.COBBLESTONE_STAIRS, Block.BRICK_STAIRS, Block.NETHER_BRICK_STAIRS,
                Block.QUARTZ_STAIRS, Block.RED_SANDSTONE_STAIRS, Block.PURPUR_STAIRS, Block.BLACKSTONE_STAIRS,
                Block.POLISHED_BLACKSTONE_STAIRS, Block.WARPED_STAIRS, Block.CRIMSON_STAIRS
        );
        List<Block> fenceGateList = List.of(
                Block.OAK_FENCE_GATE, Block.SPRUCE_FENCE_GATE, Block.BIRCH_FENCE_GATE, Block.JUNGLE_FENCE_GATE,
                Block.ACACIA_FENCE_GATE, Block.DARK_OAK_FENCE_GATE, Block.MANGROVE_FENCE_GATE, Block.BAMBOO_FENCE_GATE,
                Block.CRIMSON_FENCE_GATE, Block.WARPED_FENCE_GATE
        );
        List<Block> wallList = List.of(
                Block.STONE_BRICK_WALL, Block.COBBLESTONE_WALL, Block.MOSSY_COBBLESTONE_WALL,
                Block.BRICK_WALL, Block.NETHER_BRICK_WALL, Block.RED_NETHER_BRICK_WALL,
                Block.SANDSTONE_WALL, Block.RED_SANDSTONE_WALL, Block.PRISMARINE_WALL,
                Block.BLACKSTONE_WALL, Block.POLISHED_BLACKSTONE_WALL, Block.DEEPSLATE_BRICK_WALL
        );

        MapExtender<List<Block>, BiFunction<Block, PlayerBlockPlaceEvent, Block>> functions = new HashMapExtender<>();

        functions.putAll(Map.of(axisList, BlockFunctions::axis,
                rotationList, BlockFunctions::rotation, facingList, BlockFunctions::facing, fenceList, BlockFunctions::fence,
                slabList, BlockFunctions::slab, stairList, BlockFunctions::stair, fenceGateList, BlockFunctions::fenceGate,
                wallList, BlockFunctions::wall));

        Optional<List<Block>> blockO = functions.getKeys().stream().filter(blockL -> blockL.contains(event.getBlock())).findAny();
        if (blockO.isPresent()) {
            List<Block> blocks = blockO.get();
            event.setBlock(functions.get(blocks).apply(event.getBlock(), event));
        }
        PermissionablePlayer player = (PermissionablePlayer) event.getPlayer();
        if (player.getItemInMainHand().equals(CollisionItem.item.item()
                .withCustomName(CollisionItem.item.title())
                .withLore(CollisionItem.item.lore())
                .withCustomModelData(CollisionItem.item.customModelData()))) {
            return; // Allow the CollisionItem handlers to process it
        }
        if (CustomItemHolder.hasItem(player.getItemInMainHand()).isPresent()) {
            event.setCancelled(true);
            if (AbilitiesHolder.isAbility(player.getItemInMainHand())) {
                PlayerUseItemEvent e = new PlayerUseItemEvent(player, Objects.requireNonNull(player.getItemUseHand()), player.getItemInMainHand(), 1);
                EventDispatcher.call(e);
            }
        }
    }

    public static void onPing(ServerListPingEvent event) {
        event.getResponseData().setMaxPlayer(20);
    }

    public static void onItemUse(PlayerUseItemEvent event) {
        ItemStack item = event.getItemStack();
        if (CustomItem.getItems().contains(item) && ((PermissionablePlayer) event.getPlayer()).hasPermission(Permissions.USE_CUSTOM_ITEM)) {
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
                return;
            } else if (item.equals(DebugGui.collider)) {
                player.getInventory().addItemStack(DebugGui.collider);
                event.setCancelled(true);
                return;
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
