package src.main.commands.particlecommand;

import net.kyori.adventure.text.Component;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.Argument;
import net.minestom.server.command.builder.arguments.ArgumentEnum;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.particle.Particle;
import src.main.commands.CommandLogic;
import src.main.particlegenerator.ThreeDimenstionalParticleShapes;
import src.main.permission.Permission;
import src.main.permission.PermissionablePlayer;

public class ThreeDimensionalParticleCommand extends Command implements CommandLogic {

    public ThreeDimensionalParticleCommand() {
        super("threeparticle");

        var threeDimensionalShape = ArgumentType.Enum("shape", ThreeDimenstionalParticleShapes.class).setFormat(ArgumentEnum.Format.LOWER_CASED);
        var radiusX = ArgumentType.Double("radiusX");
        var radiusY = ArgumentType.Double("radiusY");
        var radiusZ = ArgumentType.Double("radiusZ");
        var duration = ArgumentType.Float("duration");
        var particle = ArgumentType.Particle("particle");

        defaultExecutor(this);

        argumentCallbacks(new Argument[]{threeDimensionalShape, radiusX, radiusY, radiusZ, duration, particle});

        addSyntax((sender, context) -> {
            var spawnShape = context.get(threeDimensionalShape);
            var spawnRadiusX = context.get(radiusX);
            var spawnRadiusY = context.get(radiusY);
            var spawnRadiusZ = context.get(radiusZ);
            var particleType = context.get(particle);
            var durationOfShape = context.get(duration);

            if (sender instanceof PermissionablePlayer player) {

                if (player.hasPermission(Permission.PARTICLE_SHAPE)) {
                    spawnShape.apply(player.getInstance(), player.getPosition(), spawnRadiusX, spawnRadiusY, spawnRadiusZ, particleType, durationOfShape);
                } else {
                    sender.sendMessage(Component.text("§4§lNo permission"));
                }
            } else {
                sender.sendMessage(Component.text("§4§lOnly players can use this command"));
            }
        }, threeDimensionalShape, particle, duration, radiusX, radiusY, radiusZ);
    }
}
