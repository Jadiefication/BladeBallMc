package src.main.commands;

import net.minestom.server.command.builder.Command;
import net.minestom.server.timer.Task;
import org.jetbrains.annotations.NotNull;
import src.main.Nimoh;
import src.main.core.ball.BallHandler;
import src.main.permission.Permission;
import src.main.permission.PermissionablePlayer;

public class StopBallCommand extends Command {

    public StopBallCommand() {
        super("stopball");

        setDefaultExecutor((sender, context) -> {
            try {
                PermissionablePlayer player = (PermissionablePlayer) sender;
                if (player.hasPermission(Permission.START)) {
                    BallHandler.BallState.tasks.forEach( tasks -> tasks.forEach(Task::cancel));
                    Nimoh.updateTask.cancel();
                }
            } catch (ClassCastException e) {
                BallHandler.BallState.tasks.forEach( tasks -> tasks.forEach(Task::cancel));
                Nimoh.updateTask.cancel();
            }
        });
    }
}
