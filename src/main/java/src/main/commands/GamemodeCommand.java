package src.main.commands;

import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentEnum;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.utils.entity.EntityFinder;
import src.main.permission.PermissionablePlayer;

public class GamemodeCommand extends Command {

    public GamemodeCommand() {
        super("gamemode");

        setDefaultExecutor((sender, context) -> {
            sender.sendMessage(Component.text("§4§lNo gamemode given"));
        });

        var gamemode = ArgumentType.Enum("gamemode", GameMode.class).setFormat(ArgumentEnum.Format.LOWER_CASED);
        var username = ArgumentType.Entity("player").onlyPlayers(true);

        username.setCallback(((commandSender, e) -> {
            if (MinecraftServer.getConnectionManager().findOnlinePlayer(e.getInput()) == null) {
                commandSender.sendMessage(Component.text("§4§lPlayer not found"));
            }
        }));

        gamemode.setCallback(((commandSender, e) -> {
            if (e.getInput().equalsIgnoreCase("creative") || e.getInput().equalsIgnoreCase("survival") || e.getInput().equalsIgnoreCase("spectator") || e.getInput().equalsIgnoreCase("adventure")) {
            } else {
                commandSender.sendMessage(Component.text("§4§lInvalid gamemode"));
            }
        }));

        addSyntax((commandSender, commandContext) -> {
            final GameMode switchGamemode = commandContext.get(gamemode);
            ((PermissionablePlayer) commandSender).setGameMode(switchGamemode);
        }, gamemode);

        addSyntax((sender, context) -> {
            final GameMode switchGamemode = context.get(gamemode);
            final EntityFinder entity = context.get(username);
            final Player target = entity.findFirstPlayer(sender);

            if (target == null) {
                sender.sendMessage(Component.text("§4§lPlayer not found"));
            } else {
                try {
                    target.setGameMode(switchGamemode);
                } catch (IllegalArgumentException e) {
                    sender.sendMessage(Component.text("§4§lInvalid gamemode"));
                }
            }
        }, gamemode, username);

    }
}
