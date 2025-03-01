package io.jadiefication.eventhandler.block;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.event.player.PlayerBlockPlaceEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockFace;

import java.util.List;
import java.util.Map;

public abstract class BlockFunctions {

    public static Block axis(Block block, PlayerBlockPlaceEvent e) {
        String axis;
        Vec direction = e.getPlayer().getPosition().direction();
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
        return block.withProperty("axis", axis);
    }

    public static Block rotation(Block block, PlayerBlockPlaceEvent e) {
        Vec direction = e.getPlayer().getPosition().direction();
        int rotation = (int) ((Math.atan2(direction.z(), direction.x()) * 16.0 / (2 * Math.PI) + 16.5) % 16);
        return block.withProperty("rotation", String.valueOf(rotation));
    }

    public static Block facing(Block block, PlayerBlockPlaceEvent e) {
        return block.withProperty("facing", e.getBlockFace().name().toLowerCase());
    }

    public static Block fence(Block block, PlayerBlockPlaceEvent e) {
        return block.withProperties(Map.of("east", isAround(e.getInstance(), Pos.fromPoint(e.getBlockPosition()).add(1, 0, 0)),
                "west", isAround(e.getInstance(), Pos.fromPoint(e.getBlockPosition()).add(-1, 0, 0)),
                "north", isAround(e.getInstance(), Pos.fromPoint(e.getBlockPosition()).add(0, 0, -1)),
                "south", isAround(e.getInstance(), Pos.fromPoint(e.getBlockPosition()).add(1, 0, 0))));
    }

    public static Block wall(Block block, PlayerBlockPlaceEvent e) {
        return fence(block, e).withProperty("up", String.valueOf(isAroundWalls(e, new Pos(0, 1, 0)) || isAroundWalls(e, new Pos(0, -1, 0))));
    }

    public static Block fenceGate(Block block, PlayerBlockPlaceEvent e) {
        return facing(block, e).withProperty("in_wall", String.valueOf(isAroundWalls(e, new Pos(-1, 0, 0))
                || isAroundWalls(e, new Pos(0, 0, 0))
                || isAroundWalls(e, new Pos(0, 0, -1))
                || isAroundWalls(e, new Pos(0, 0, 1))
        ));
    }

    public static Block slab(Block block, PlayerBlockPlaceEvent e) {
        if (e.getInstance().getBlock(e.getBlockPosition()) == block) {
            return block.withProperty("type", "double");
        } else {
            return block.withProperty("type", e.getBlockPosition().y() % 1.0 > 0.5 ? "top" : "bottom");
        }
    }

    public static Block stair(Block block, PlayerBlockPlaceEvent e) {
        BlockFace face = e.getBlockFace();
        Pos pos = Pos.fromPoint(e.getBlockPosition());
        Instance instance = e.getInstance();
        String shape = "straight";
        
        // Check adjacent blocks for stairs of the same type
        boolean north = isAroundSameBlocks(instance, pos.add(0, 0, -1), block);
        boolean south = isAroundSameBlocks(instance, pos.add(0, 0, 1), block);
        boolean east = isAroundSameBlocks(instance, pos.add(1, 0, 0), block);
        boolean west = isAroundSameBlocks(instance, pos.add(-1, 0, 0), block);

        switch (face) {
            case NORTH -> {
                if (east && instance.getBlock(pos.add(1, 0, -1)) == block) shape = "inner_right";
                else if (west && instance.getBlock(pos.add(-1, 0, -1)) == block) shape = "inner_left";
                else if (east) shape = "outer_left";
                else if (west) shape = "outer_right";
            }
            case SOUTH -> {
                if (east && instance.getBlock(pos.add(1, 0, 1)) == block) shape = "inner_left";
                else if (west && instance.getBlock(pos.add(-1, 0, 1)) == block) shape = "inner_right";
                else if (east) shape = "outer_right";
                else if (west) shape = "outer_left";
            }
            case EAST -> {
                if (north && instance.getBlock(pos.add(1, 0, -1)) == block) shape = "inner_left";
                else if (south && instance.getBlock(pos.add(1, 0, 1)) == block) shape = "inner_right";
                else if (north) shape = "outer_right";
                else if (south) shape = "outer_left";
            }
            case WEST -> {
                if (north && instance.getBlock(pos.add(-1, 0, -1)) == block) shape = "inner_right";
                else if (south && instance.getBlock(pos.add(-1, 0, 1)) == block) shape = "inner_left";
                else if (north) shape = "outer_left";
                else if (south) shape = "outer_right";
            }
        }

        return facing(block, e).withProperties(Map.of(
            "half", e.getBlockPosition().y() % 1.0 > 0.5 ? "top" : "bottom",
            "shape", shape
        ));
    }

    private static String isAround(Instance instance, Pos pos) {
        return String.valueOf(instance.getBlock(pos).isSolid());
    }

    private static boolean isAroundSameBlocks(Instance instance, Pos pos, Block block) {
        return instance.getBlock(pos) == block;
    }

    private static boolean isAroundWalls(PlayerBlockPlaceEvent e, Pos pos) {
        List<Block> wallList = List.of(
                Block.STONE_BRICK_WALL, Block.COBBLESTONE_WALL, Block.MOSSY_COBBLESTONE_WALL,
                Block.BRICK_WALL, Block.NETHER_BRICK_WALL, Block.RED_NETHER_BRICK_WALL,
                Block.SANDSTONE_WALL, Block.RED_SANDSTONE_WALL, Block.PRISMARINE_WALL,
                Block.BLACKSTONE_WALL, Block.POLISHED_BLACKSTONE_WALL, Block.DEEPSLATE_BRICK_WALL
        );

        return wallList.contains(e.getInstance().getBlock(e.getBlockPosition().add(pos)));
    }
}
