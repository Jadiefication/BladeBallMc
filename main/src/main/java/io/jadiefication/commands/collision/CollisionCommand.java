package io.jadiefication.commands.collision;

import io.jadiefication.commands.CommandLogic;
import io.jadiefication.game.prestart.collision.CollisionHandler;
import io.jadiefication.permission.PermissionablePlayer;
import io.jadiefication.game.prestart.collision.CollisionItem;
import io.jadiefication.permission.Permissions;
import net.kyori.adventure.text.Component;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.Argument;
import net.minestom.server.command.builder.arguments.ArgumentEnum;
import net.minestom.server.command.builder.arguments.ArgumentType;

public class CollisionCommand extends Command implements CommandLogic {

    public CollisionCommand() {
        super("collision");

        var action = ArgumentType.Enum("action", CollisionAction.class).setFormat(ArgumentEnum.Format.LOWER_CASED);
        var name = ArgumentType.String("name");

        defaultExecutor(this);
        argumentCallbacks(new Argument[]{action, name});

        addSyntax((sender, context) -> {
            CollisionAction doAction = context.get(action);
            String areaName = context.get(name);
            if (sender instanceof PermissionablePlayer player) {
                if (player.hasPermission(Permissions.OP)) {
                    if (doAction.equals(CollisionAction.CREATE)) {
                        if (CollisionItem.isSelected()) {
                            CollisionItem.createArea(areaName);
                        } else {
                            sender.sendMessage(Component.text("§4§lNo area selected"));
                        }
                    } else if (doAction.equals(CollisionAction.DELETE)) {
                        if (CollisionHandler.areas.containsKey(areaName)) {
                            CollisionHandler.areas.get(areaName).hide();
                            CollisionHandler.areas.remove(areaName);
                        } else {
                            sender.sendMessage(Component.text("§4§lNo area exists with that name"));
                        }
                    }
                } else {
                    sender.sendMessage(Component.text("§4§lNo permission"));
                }
            } else {
                sender.sendMessage(Component.text("§4§lOnly players can use this command"));
            }
        }, action, name);
    }
}
