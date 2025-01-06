package src.main.commands;

import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.Player;
import net.minestom.server.utils.entity.EntityFinder;
import src.main.permission.Permission;
import src.main.permission.PermissionablePlayer;

public class OpCommand extends Command {

    public OpCommand() {
        super("op");

        setDefaultExecutor((sender, context) -> {
            sender.sendMessage(Component.text("§4§lSomething went wrong"));
        });

        var userName = ArgumentType.Entity("user").onlyPlayers(true);

        userName.setCallback(((commandSender, e) -> {
            if (MinecraftServer.getConnectionManager().findOnlinePlayer(e.getInput()) == null) {
                commandSender.sendMessage(Component.text("§4§lPlayer not found"));
            }
        }));

        addSyntax((commandSender, commandContext) -> {
            final EntityFinder finder = commandContext.get(userName);
            final Player target = finder.findFirstPlayer(commandSender);
            if (commandSender instanceof PermissionablePlayer player) {

                if (player.hasPermission(Permission.OP)) {
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
            if (commandSender instanceof PermissionablePlayer player && (player.getPermissionLevel() == 4 || player.hasPermission(Permission.OP))) {
                player.setPermissionLevel(4);
            }
        });
    }
}
