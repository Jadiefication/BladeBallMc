package io.jadiefication.commands.arguments;

import net.minestom.server.command.builder.arguments.ArgumentType;
import org.jetbrains.annotations.NotNull;

public class CustomArgumentTypes extends ArgumentType {

    public static ArgumentRecord Record(@NotNull String id, @NotNull Class<? extends Record> recordClass) {
        return new ArgumentRecord(id, recordClass);
    }
}
