package io.jadiefication.commands.arguments;

import net.minestom.server.command.builder.arguments.ArgumentType;
import org.jetbrains.annotations.NotNull;

public class CustomArgumentTypes extends ArgumentType {

    public static ArgumentRecord Particle(@NotNull String id, @NotNull Class<Record> recordClass) {
        return new ArgumentRecord(id, recordClass);
    }
}
