package src.main.commands.particlecommand;

import net.kyori.adventure.text.Component;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentEnum;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.particle.Particle;
import org.jetbrains.annotations.NotNull;
import src.main.particlegenerator.ThreeDimenstionalParticleShapes;
import src.main.particlegenerator.TwoDimensionalParticleShapes;
import src.main.permission.Permission;
import src.main.permission.PermissionablePlayer;

public class ThreeDimensionalParticleCommand extends Command {

    public ThreeDimensionalParticleCommand() {
        super("threeparticle");

        var threeDimensionalShape = ArgumentType.Enum("shape", ThreeDimenstionalParticleShapes.class).setFormat(ArgumentEnum.Format.LOWER_CASED);
        var radiusX = ArgumentType.Double("radiusX");
        var radiusY = ArgumentType.Double("radiusY");
        var radiusZ = ArgumentType.Double("radiusZ");
        var duration = ArgumentType.Float("duration");
        var particle = ArgumentType.Particle("particle");

        setDefaultExecutor((sender, context) -> {
            sender.sendMessage("§4§lNo arguments given");
        });

        threeDimensionalShape.setCallback((sender, e) -> {
            try {
                ThreeDimenstionalParticleShapes.valueOf(e.getInput());
            } catch (IllegalArgumentException exception) {
                sender.sendMessage("§4§lInvalid shape");
            }
        });

        particle.setCallback((sender, e) -> {
            if(Particle.fromNamespaceId(e.getInput()) == null) sender.sendMessage(Component.text("§4§lIncorrect particle type"));
        });

        radiusX.setCallback((sender, e) -> {
            try {
                if (Double.parseDouble(e.getInput()) < 0) {
                    sender.sendMessage(Component.text("§4§lRadius must be positive"));
                }
            } catch (NumberFormatException ex) {
                sender.sendMessage(Component.text("§4§lRadius must be a float"));
            }
        });

        radiusZ.setCallback((sender, e) -> {
            try {
                if (Double.parseDouble(e.getInput()) < 0) {
                    sender.sendMessage(Component.text("§4§lRadius must be positive"));
                }
            } catch (NumberFormatException ex) {
                sender.sendMessage(Component.text("§4§lRadius must be a float"));
            }
        });

        radiusY.setCallback((sender, e) -> {
            try {
                if (Double.parseDouble(e.getInput()) < 0) {
                    sender.sendMessage(Component.text("§4§lRadius must be positive"));
                }
            } catch (NumberFormatException ex) {
                sender.sendMessage(Component.text("§4§lRadius must be a float"));
            }
        });

        duration.setCallback((sender, e) -> {
            try {
                if (Double.parseDouble(e.getInput()) < 0) {
                    sender.sendMessage(Component.text("§4§lRadius must be positive"));
                }
            } catch (NumberFormatException ex) {
                sender.sendMessage(Component.text("§4§lRadius must be a float"));
            }
        });

        addSyntax((sender, context) -> {
            var spawnShape = context.get(threeDimensionalShape);
            var spawnRadiusX = context.get(radiusX);
            var spawnRadiusY = context.get(radiusY);
            var spawnRadiusZ = context.get(radiusZ);
            var particleType = context.get(particle);
            var durationOfShape = context.get(duration);

            PermissionablePlayer player = (PermissionablePlayer) sender;

            if (player.hasPermission(Permission.PARTICLE_SHAPE)) {
                spawnShape.apply(player.getInstance(), player.getPosition(), spawnRadiusX, spawnRadiusY, spawnRadiusZ, particleType, durationOfShape);
            } else {
                sender.sendMessage(Component.text("§4§lNo permission"));
            }
        }, threeDimensionalShape, particle, duration, radiusX, radiusY, radiusZ);
    }
}
