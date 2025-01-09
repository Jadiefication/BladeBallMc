package src.main.commands;

import net.minestom.server.command.builder.Command;
import src.main.Nimoh;
import src.main.permission.Permission;
import src.main.permission.PermissionablePlayer;

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
