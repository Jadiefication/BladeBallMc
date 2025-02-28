package io.jadiefication.commands.timecommand;

import io.jadiefication.commands.Action;
import io.jadiefication.commands.CommandLogic;
import io.jadiefication.permission.PermissionablePlayer;
import io.jadiefication.permission.Permissions;
import net.kyori.adventure.text.Component;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.Argument;
import net.minestom.server.command.builder.arguments.ArgumentEnum;
import net.minestom.server.command.builder.arguments.ArgumentType;

public class TimeCommand extends Command implements CommandLogic {

    public TimeCommand() {
        super("time");

        var action = ArgumentType.Enum("action", Action.class).setFormat(ArgumentEnum.Format.LOWER_CASED);
        var type = ArgumentType.Enum("time", Time.class).setFormat(ArgumentEnum.Format.LOWER_CASED);

        defaultExecutor(this);

        argumentCallbacks(new Argument[]{action, type});

        addSyntax((sender, context) -> {
            final Action doAction = context.get(action);
            final Time time = context.get(type);
            if (sender instanceof PermissionablePlayer player) {

                if (player.hasPermission(Permissions.TIME)) {
                    if (doAction.equals(Action.SET)) player.getInstance().setTime(time.getTicks());
                }
            } else {
                sender.sendMessage(Component.text("§4§lOnly players can use this command"));
            }

        }, action, type);
    }
}
