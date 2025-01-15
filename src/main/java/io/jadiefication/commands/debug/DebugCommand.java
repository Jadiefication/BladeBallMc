package io.jadiefication.commands.debug;

import net.kyori.adventure.text.Component;
import net.minestom.server.command.builder.Command;
import io.jadiefication.commands.debug.gui.DebugGui;
import io.jadiefication.permission.Permission;
import io.jadiefication.permission.PermissionablePlayer;

public class DebugCommand extends Command {

    public DebugCommand() {
        super("debug");

        setDefaultExecutor((sender, context) -> {
            PermissionablePlayer player;
            try {
                player = (PermissionablePlayer) sender;
                if (player.hasPermission(Permission.OP)) player.openInventory(new DebugGui());
            } catch (ClassCastException e) {
                sender.sendMessage(Component.text("§4§lOnly a player can run this command"));
            }
        });
    }
}
