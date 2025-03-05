package io.jadiefication.commands.collision.visibility;

import io.jadiefication.permission.PermissionablePlayer;
import io.jadiefication.permission.Permissions;
import io.jadiefication.game.prestart.collision.CollisionHandler;
import net.minestom.server.command.builder.Command;

public class ShowCommand extends Command {

    public ShowCommand() {
        super("show");

        setDefaultExecutor((sender, context) -> {
            if (sender instanceof PermissionablePlayer player) {
                if (player.hasPermission(Permissions.OP)) {
                    CollisionHandler.areas.getValues().forEach(area -> area.show(player));
                }
            }
        });
    }
}
