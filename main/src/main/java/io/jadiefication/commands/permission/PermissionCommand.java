package io.jadiefication.commands.permission;

import io.jadiefication.commands.Action;
import io.jadiefication.commands.CommandLogic;
import io.jadiefication.commands.arguments.CustomArgumentTypes;
import io.jadiefication.commands.timecommand.Time;
import io.jadiefication.permission.PermissionHandler;
import io.jadiefication.permission.PermissionablePlayer;
import io.jadiefication.permission.Permissions;
import net.kyori.adventure.text.Component;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.Argument;
import net.minestom.server.command.builder.arguments.ArgumentEnum;
import net.minestom.server.command.builder.arguments.ArgumentType;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.RecordComponent;

public class PermissionCommand extends Command implements CommandLogic {

    public PermissionCommand() {
        super("permission");

        var action = ArgumentType.Enum("action", PermissionAction.class).setFormat(ArgumentEnum.Format.LOWER_CASED);
        var username = ArgumentType.Entity("player").onlyPlayers(true);
        var permission = CustomArgumentTypes.Record("permission", Permissions.class);

        defaultExecutor(this);

        argumentCallbacks(new Argument[]{action, permission});

        addSyntax((sender, context) -> {
            final PermissionAction doAction = context.get(action);
            final PermissionablePlayer player = (PermissionablePlayer) context.get(username).findFirstPlayer(sender);
            final RecordComponent _permission = context.get(permission);
            if (sender instanceof PermissionablePlayer _player) {

                if (_player.hasPermission(Permissions.getPermission("OP"))) {
                    if (player != null) {
                        if (doAction.equals(PermissionAction.ADD)) {
                            PermissionHandler.addPermission(player, Permissions.getPermission(_permission.getName()));
                        } else if (doAction.equals(PermissionAction.DELETE)) {
                            PermissionHandler.removePermission(player, Permissions.getPermission(_permission.getName()));
                        }
                    } else {
                        sender.sendMessage(Component.text("§4§lDesired player isn't online"));
                    }
                }
            } else {
                sender.sendMessage(Component.text("§4§lOnly players can use this command"));
            }
        }, action, username, permission);
    }
}
