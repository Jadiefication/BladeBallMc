package io.jadiefication.commands;

import net.minestom.server.command.builder.Command;
import net.minestom.server.timer.Task;
import io.jadiefication.Nimoh;
import io.jadiefication.core.ball.BallHandler;
import io.jadiefication.permission.Permission;
import io.jadiefication.permission.PermissionablePlayer;

public class StopBallCommand extends Command {

    public StopBallCommand() {
        super("stopball");

        setDefaultExecutor((sender, context) -> {
            try {
                PermissionablePlayer player = (PermissionablePlayer) sender;
                if (player.hasPermission(Permission.START)) {
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
