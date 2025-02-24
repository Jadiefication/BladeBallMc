package io.jadiefication.commands;

import io.jadiefication.permission.PermissionablePlayer;
import io.jadiefication.permission.Permissions;
import net.kyori.adventure.text.Component;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.Player;
import net.minestom.server.utils.entity.EntityFinder;

public class OpCommand extends Command implements CommandLogic {

    public OpCommand() {
        super("op");

        var userName = ArgumentType.Entity("user").onlyPlayers(true);

        defaultExecutor(this);

        argumentCallbacks(userName);

        addSyntax((commandSender, commandContext) -> {
            final EntityFinder finder = commandContext.get(userName);
            final Player target = finder.findFirstPlayer(commandSender);
            if (commandSender instanceof PermissionablePlayer player) {

                if (player.hasPermission(Permissions.getPermission("OP"))) {
                    if (target != null) {
                        target.setPermissionLevel(4);
                    } else {
                        commandSender.sendMessage(Component.text("§4§lNo player with that name"));
                    }
                } else {
                    commandSender.sendMessage(Component.text("§4§lNo permission"));
                }
            } else {
                if (target != null) {
                    target.setPermissionLevel(4);
                } else {
                    commandSender.sendMessage(Component.text("§4§lNo player with that name"));
                }
            }
        }, userName);

        addSyntax((commandSender, context) -> {
            if (commandSender instanceof PermissionablePlayer player && (player.getPermissionLevel() == 4 || player.hasPermission(Permissions.getPermission("OP")))) {
                player.setPermissionLevel(4);
            }
        });
    }
}
