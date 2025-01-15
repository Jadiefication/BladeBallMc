package io.jadiefication.commands.weathercommand;

import net.kyori.adventure.text.Component;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.Argument;
import net.minestom.server.command.builder.arguments.ArgumentEnum;
import net.minestom.server.command.builder.arguments.ArgumentType;
import io.jadiefication.commands.CommandLogic;
import io.jadiefication.permission.Permission;
import io.jadiefication.permission.PermissionablePlayer;

public class WeatherCommand extends Command implements CommandLogic {
    public WeatherCommand() {
        super("weather");

        var action = ArgumentType.String("action");
        var type = ArgumentType.Enum("weather", WeatherEnum.class).setFormat(ArgumentEnum.Format.LOWER_CASED);

        defaultExecutor(this);

        argumentCallbacks(new Argument[]{action, type});

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
