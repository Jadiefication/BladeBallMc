package src.main.commands;

import net.kyori.adventure.text.Component;
import net.minestom.server.command.builder.ArgumentCallback;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.Argument;
import net.minestom.server.command.builder.arguments.ArgumentEnum;
import net.minestom.server.command.builder.arguments.ArgumentType;

import java.util.Map;
import java.util.function.Consumer;

public interface CommandLogic {

    default void defaultExecutor(Command self) {
        self.setDefaultExecutor((sender, context) -> {
            sender.sendMessage(Component.text("§4§lNo argument given"));
        });
    }

    default <T> void argumentCallbacks(Argument<T>... arguments) {
        for (Argument<T> argument : arguments) {
            argument.setCallback((sender, exception) -> {
                sender.sendMessage(Component.text("§4§lInvalid argument"));
            });
        }
    }
}