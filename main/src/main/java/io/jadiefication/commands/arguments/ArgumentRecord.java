package io.jadiefication.commands.arguments;

import net.minestom.server.command.ArgumentParserType;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.arguments.Argument;
import net.minestom.server.command.builder.exception.ArgumentSyntaxException;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.RecordComponent;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.UnaryOperator;

public class ArgumentRecord extends Argument<RecordComponent> {
    public final static int NOT_RECORD_VALUE_ERROR = 1;

    private final Class<Record> recordClass;
    private final RecordComponent[] values;
    private ArgumentRecord.Format format = ArgumentRecord.Format.DEFAULT;

    public ArgumentRecord(@NotNull String id, Class<Record> recordClass) {
        super(id);
        this.recordClass = recordClass;
        this.values = recordClass.getRecordComponents();
    }

    public ArgumentRecord setFormat(@NotNull ArgumentRecord.Format format) {
        this.format = format;
        return this;
    }

    @Override
    public @NotNull RecordComponent parse(@NotNull CommandSender sender, @NotNull String input) throws ArgumentSyntaxException {
        for (RecordComponent value : this.values) {
            if (this.format.formatter.apply(value.getName()).equals(input)) {
                return value;
            }
        }
        throw new ArgumentSyntaxException("Not a " + this.recordClass.getSimpleName() + " value", input, NOT_RECORD_VALUE_ERROR);
    }

    @Override
    public ArgumentParserType parser() {
        return null;
    }

    public List<String> entries() {
        return Arrays.stream(values).map(x -> format.formatter.apply(x.getName())).toList();
    }

    public enum Format {
        DEFAULT(name -> name),
        LOWER_CASED(name -> name.toLowerCase(Locale.ROOT)),
        UPPER_CASED(name -> name.toUpperCase(Locale.ROOT));

        private final UnaryOperator<String> formatter;

        Format(@NotNull UnaryOperator<String> formatter) {
            this.formatter = formatter;
        }
    }

    @Override
    public String toString() {
        return String.format("Record<%s>", getId());
    }
}
