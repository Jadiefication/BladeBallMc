package io.jadiefication.commands;

import io.jadiefication.permission.PermissionablePlayer;
import net.kyori.adventure.text.Component;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.Argument;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.block.Block;

public class FillCommand extends Command implements CommandLogic {

    public FillCommand() {
        super("fill");

        var startPoint = ArgumentType.RelativeBlockPosition("start");
        var endPoint = ArgumentType.RelativeBlockPosition("end");
        var block = ArgumentType.BlockState("block");

        defaultExecutor(this);

        argumentCallbacks(new Argument[]{startPoint, endPoint, block});

        addSyntax((sender, context) -> {

            final Block blockToReplaceWith = context.get(block);
            if (sender instanceof PermissionablePlayer player) {
                final Vec start = context.get(startPoint).from(player);
                final Vec end = context.get(endPoint).from(player);

                int minX = Math.min((int) Math.floor(start.x()), (int) Math.floor(end.x()));
                int maxX = Math.max((int) Math.floor(start.x()), (int) Math.floor(end.x()));
                int minY = Math.min((int) Math.floor(start.y()), (int) Math.floor(end.y()));
                int maxY = Math.max((int) Math.floor(start.y()), (int) Math.floor(end.y()));
                int minZ = Math.min((int) Math.floor(start.z()), (int) Math.floor(end.z()));
                int maxZ = Math.max((int) Math.floor(start.z()), (int) Math.floor(end.z()));

                for (int x = minX; x <= maxX; x++) {
                    for (int y = minY; y <= maxY; y++) {
                        for (int z = minZ; z <= maxZ; z++) {
                            player.getInstance().setBlock(x, y, z, blockToReplaceWith);
                        }
                    }
                }
            } else {
                sender.sendMessage(Component.text("§4§lOnly players can use this command"));
            }

        }, startPoint, endPoint, block);
    }
}
