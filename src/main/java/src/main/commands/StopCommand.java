package src.main.commands;

import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.Command;
import src.main.permission.Permission;
import src.main.permission.PermissionablePlayer;

public class StopCommand extends Command {

    public StopCommand() {
        super("stop");

        setDefaultExecutor((sender, context) -> {
            if (sender instanceof PermissionablePlayer player) {
                if (player.hasPermission(Permission.STOP)) MinecraftServer.stopCleanly();
                else player.sendMessage(Component.text("§4§lNo permission"));
            } else {
                MinecraftServer.stopCleanly();
            }
        });
    }
}
