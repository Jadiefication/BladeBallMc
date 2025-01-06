package src.main.commands.weathercommand;

import net.kyori.adventure.text.Component;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentEnum;
import net.minestom.server.command.builder.arguments.ArgumentType;
import src.main.permission.Permission;
import src.main.permission.PermissionablePlayer;

public class WeatherCommand extends Command {
    public WeatherCommand() {
        super("weather");

        setDefaultExecutor(((sender, context) -> {
            sender.sendMessage(Component.text("§4§lNo arguments given."));
        }));

        var action = ArgumentType.String("action");
        var type = ArgumentType.Enum("weather", WeatherEnum.class).setFormat(ArgumentEnum.Format.LOWER_CASED);

        action.setCallback((sender, exception) -> {
            sender.sendMessage(Component.text("§4§lIncorrect Action"));
        });

        type.setCallback((sender, exception) -> {
            sender.sendMessage(Component.text("§4§lIncorrect Weather"));
        });

        addSyntax((sender, context) -> {
            final String doAction = context.get(action);
            final WeatherEnum weather = context.get(type);
            if (sender instanceof PermissionablePlayer player) {

                if (player.hasPermission(Permission.WEATHER)) {
                    if (doAction.equalsIgnoreCase("set")) player.getInstance().setWeather(weather.getWeather());
                }
            } else {
                sender.sendMessage(Component.text("§4§lOnly players can use this command"));
            }

        }, action, type);
    }
}
