package io.jadiefication.commands;

import io.jadiefication.Nimoh;
import io.jadiefication.core.ball.BallHandler;
import io.jadiefication.permission.PermissionablePlayer;
import io.jadiefication.permission.Permissions;
import net.minestom.server.command.builder.Command;

public class StopBallCommand extends Command {

    public StopBallCommand() {
        super("stopball");

        setDefaultExecutor((sender, context) -> {
            try {
                PermissionablePlayer player = (PermissionablePlayer) sender;
                if (player.hasPermission(Permissions.getPermission("START"))) {
                    BallHandler.BallState.task.cancel();
                    Nimoh.updateTask.cancel();
                }
            } catch (ClassCastException e) {
                BallHandler.BallState.task.cancel();
                Nimoh.updateTask.cancel();
            }
        });
    }
}
