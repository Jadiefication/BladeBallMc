package src.main.commands;

import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.Command;

public class StopCommand extends Command {

    public StopCommand() {
        super("stop");

        setDefaultExecutor((sender, context) -> {
            sender.sendMessage("§4§lServer shutting down");
            MinecraftServer.stopCleanly();
        });

        addSyntax((sender, context) -> {
            sender.sendMessage("§4§lServer shutting down");
            MinecraftServer.stopCleanly();
        });
    }
}
