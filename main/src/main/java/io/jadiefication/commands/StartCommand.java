package io.jadiefication.commands;

import io.jadiefication.Nimoh;
import io.jadiefication.permission.Permission;
import io.jadiefication.permission.PermissionablePlayer;
import net.minestom.server.command.builder.Command;

public class StartCommand extends Command
{
    public StartCommand() {
        super("start");

        setDefaultExecutor((sender, context) -> {
            try {
                PermissionablePlayer player = (PermissionablePlayer) sender;
                if (player.hasPermission(Permission.START)) Nimoh.startBladeBall();
            } catch (ClassCastException e) {
                Nimoh.startBladeBall();
            }
        });
    }
}
