package io.jadiefication.commands;

import net.minestom.server.command.builder.Command;
import io.jadiefication.Nimoh;
import io.jadiefication.permission.Permission;
import io.jadiefication.permission.PermissionablePlayer;

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
