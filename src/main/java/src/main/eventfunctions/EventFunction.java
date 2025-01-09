package src.main.eventfunctions;

import net.kyori.adventure.text.Component;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.GameMode;
import net.minestom.server.event.inventory.InventoryPreClickEvent;
import net.minestom.server.event.player.*;
import net.minestom.server.event.server.ServerListPingEvent;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockFace;
import net.minestom.server.inventory.AbstractInventory;
import net.minestom.server.item.ItemStack;
import src.main.Nimoh;
import src.main.Server;
import src.main.commands.debug.gui.DebugGui;
import src.main.core.GUI.Border;
import src.main.customitem.CustomItem;
import src.main.permission.Permission;
import src.main.permission.PermissionablePlayer;

import java.util.List;
import java.util.Objects;

public abstract class EventFunction {

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

    public static void onUse(PlayerUseItemEvent event) {
        ItemStack item = event.getItemStack();
        if (CustomItem.getItemMap().containsKey(item) && ((PermissionablePlayer) event.getPlayer()).hasPermission(Permission.USE_CUSTOM_ITEM)) {
            CustomItem.getItemFunctionality(item).accept(event);
        }
    }

    public static void onJoin(AsyncPlayerConfigurationEvent event) {
        final PermissionablePlayer player = (PermissionablePlayer) event.getPlayer();
        Server.sendPackInfo(player);
        if (player.getName().equals(Component.text("Jadiefication"))) {
            player.setPermissionLevel(4);
        }
        event.setSpawningInstance(Nimoh.instanceContainer);
        player.setRespawnPoint(new Pos(0.0, 44.0, 0.0));
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

        if (player.hasPermission(Permission.PLACE) && !player.getGameMode().equals(GameMode.ADVENTURE)) {
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
        } else {
            event.setCancelled(true);
        }
    }

    public static void onPing(ServerListPingEvent event) {
        event.getResponseData().setMaxPlayer(20);
    }

    public static void onInventoryClick(InventoryPreClickEvent event) {
        AbstractInventory inventory = event.getInventory();

        if (inventory instanceof DebugGui) {
            ItemStack item = event.getClickedItem();
            if (item.equals(Border.border)) {
                event.setCancelled(true);
            } else if (item.equals(DebugGui.createPerformanceCheckerItem())) {
                CustomItem.getItemFunctionality(DebugGui.createPerformanceCheckerItem()).accept(event);
            }
        }
    }
}
