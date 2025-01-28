package io.jadiefication.commands;

import io.jadiefication.permission.Permission;
import io.jadiefication.permission.PermissionablePlayer;
import net.kyori.adventure.text.Component;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.Argument;
import net.minestom.server.command.builder.arguments.ArgumentEnum;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.utils.entity.EntityFinder;

public class GamemodeCommand extends Command implements CommandLogic {

    public GamemodeCommand() {
        super("gamemode");

        var gamemode = ArgumentType.Enum("gamemode", GameMode.class).setFormat(ArgumentEnum.Format.LOWER_CASED);
        var username = ArgumentType.Entity("player").onlyPlayers(true);

        defaultExecutor(this);

        argumentCallbacks(new Argument[]{gamemode, username});

        addSyntax((commandSender, commandContext) -> {
            final GameMode switchGamemode = commandContext.get(gamemode);
            if (commandSender instanceof PermissionablePlayer player && player.hasPermission(Permission.GAMEMODE)) player.setGameMode(switchGamemode);
            else {
                commandSender.sendMessage(Component.text("§4§lOnly players can use this command"));
            }
        }, gamemode);

        addSyntax((sender, context) -> {
            final GameMode switchGamemode = context.get(gamemode);
            final EntityFinder entity = context.get(username);
            final Player target = entity.findFirstPlayer(sender);

            if (sender instanceof PermissionablePlayer player) {

                if (player.hasPermission(Permission.GAMEMODE)) {
                    setGamemode(target, switchGamemode, sender);
                } else {
                    sender.sendMessage(Component.text("§4§lNo permission"));
                }
            } else {
                setGamemode(target, switchGamemode, sender);
            }
        }, gamemode, username);

    }

    private static void setGamemode(Player target, GameMode gameMode, CommandSender sender) {
        if (target == null) {
            sender.sendMessage(Component.text("§4§lPlayer not found"));
        } else {
            target.setGameMode(gameMode);
        }
    }
}
