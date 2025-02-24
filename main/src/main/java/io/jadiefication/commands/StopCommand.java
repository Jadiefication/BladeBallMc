package io.jadiefication.commands;

import io.jadiefication.permission.PermissionablePlayer;
import io.jadiefication.permission.Permissions;
import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.Command;

public class StopCommand extends Command {

    public StopCommand() {
        super("stop");

        setDefaultExecutor((sender, context) -> {
            if (sender instanceof PermissionablePlayer player) {
                if (player.hasPermission(Permissions.getPermission("STOP"))) MinecraftServer.stopCleanly();
                else player.sendMessage(Component.text("§4§lNo permission"));
            } else {
                MinecraftServer.stopCleanly();
            }
        });
    }
}
