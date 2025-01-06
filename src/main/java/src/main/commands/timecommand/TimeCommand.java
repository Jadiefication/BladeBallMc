package src.main.commands.timecommand;

import net.kyori.adventure.text.Component;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentEnum;
import net.minestom.server.command.builder.arguments.ArgumentType;
import src.main.permission.Permission;
import src.main.permission.PermissionablePlayer;

public class TimeCommand extends Command {

    public TimeCommand() {
        super("time");

        setDefaultExecutor(((sender, context) -> {
            sender.sendMessage(Component.text("§4§lNo arguments given."));
        }));

        var action = ArgumentType.String("action");
        var type = ArgumentType.Enum("time", Time.class).setFormat(ArgumentEnum.Format.LOWER_CASED);

        action.setCallback((sender, exception) -> {
            sender.sendMessage(Component.text("§4§lIncorrect Action"));
        });

        type.setCallback((sender, exception) -> {
            sender.sendMessage(Component.text("§4§lIncorrect Time"));
        });

        addSyntax((sender, context) -> {
            final String doAction = context.get(action);
            final Time time = context.get(type);
            if (sender instanceof PermissionablePlayer player) {

                if (player.hasPermission(Permission.TIME)) {
                    if (doAction.equalsIgnoreCase("set")) player.getInstance().setTime(time.getTicks());
                }
            } else {
                sender.sendMessage(Component.text("§4§lOnly players can use this command"));
            }

        }, action, type);
    }
}
