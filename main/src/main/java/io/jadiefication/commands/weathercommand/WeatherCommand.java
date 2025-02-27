package io.jadiefication.commands.weathercommand;

import io.jadiefication.commands.Action;
import io.jadiefication.commands.CommandLogic;
import io.jadiefication.permission.PermissionablePlayer;
import io.jadiefication.permission.Permissions;
import net.kyori.adventure.text.Component;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.Argument;
import net.minestom.server.command.builder.arguments.ArgumentEnum;
import net.minestom.server.command.builder.arguments.ArgumentType;

public class WeatherCommand extends Command implements CommandLogic {
    public WeatherCommand() {
        super("weather");

        var action = ArgumentType.Enum("action", Action.class).setFormat(ArgumentEnum.Format.LOWER_CASED);
        var type = ArgumentType.Enum("weather", WeatherEnum.class).setFormat(ArgumentEnum.Format.LOWER_CASED);

        defaultExecutor(this);

        argumentCallbacks(new Argument[]{action, type});

        addSyntax((sender, context) -> {
            final Action doAction = context.get(action);
            final WeatherEnum weather = context.get(type);
            if (sender instanceof PermissionablePlayer player) {

                if (player.hasPermission(Permissions.WEATHER)) {
                    if (doAction.equals(Action.SET)) player.getInstance().setWeather(weather.getWeather());
                }
            } else {
                sender.sendMessage(Component.text("§4§lOnly players can use this command"));
            }

        }, action, type);
    }
}
