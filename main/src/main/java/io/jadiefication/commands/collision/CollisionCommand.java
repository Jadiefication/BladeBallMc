package io.jadiefication.commands.collision;

import io.jadiefication.commands.CommandLogic;
import io.jadiefication.permission.PermissionablePlayer;
import io.jadiefication.game.prestart.collision.CollisionItem;
import net.kyori.adventure.text.Component;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentEnum;
import net.minestom.server.command.builder.arguments.ArgumentType;

public class CollisionCommand extends Command implements CommandLogic {

    public CollisionCommand() {
        super("collision");

        var action = ArgumentType.Enum("action", CollisionAction.class).setFormat(ArgumentEnum.Format.LOWER_CASED);
        var name = ArgumentType.String("name");

        defaultExecutor(this);
        argumentCallbacks(action);

        addSyntax((sender, context) -> {
            CollisionAction doAction = context.get(action);
            String areaName = context.get(name);
            if (sender instanceof PermissionablePlayer player) {
                if (doAction.equals(CollisionAction.CREATE)) {
                    if (CollisionItem.isSelected()) {
                        CollisionItem.createArea(areaName);
                    } else {

                    }
                } else {
                    sender.sendMessage(Component.text("§4§lNo permission"));
                }
            }
        }, action, name);
    }
}
