package io.jadiefication.commands.particlecommand;

import net.kyori.adventure.text.Component;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.Argument;
import net.minestom.server.command.builder.arguments.ArgumentEnum;
import net.minestom.server.command.builder.arguments.ArgumentType;
import io.jadiefication.commands.CommandLogic;
import io.jadiefication.particlegenerator.TwoDimensionalParticleShapes;
import io.jadiefication.permission.Permission;
import io.jadiefication.permission.PermissionablePlayer;

public class TwoDimensionalParticleCommand extends Command implements CommandLogic {

    public TwoDimensionalParticleCommand() {
        super("twoparticle");

        var twoDimensionalShape = ArgumentType.Enum("shape", TwoDimensionalParticleShapes.class).setFormat(ArgumentEnum.Format.LOWER_CASED);
        var radiusX = ArgumentType.Double("radiusX");
        var radiusY = ArgumentType.Double("radiusY");
        var duration = ArgumentType.Float("duration");
        var particle = ArgumentType.Particle("particle");

        defaultExecutor(this);

        argumentCallbacks(new Argument[]{twoDimensionalShape, radiusX, radiusY, duration, particle});

        addSyntax((sender, context) -> {
            var spawnShape = context.get(twoDimensionalShape);
            var spawnRadiusX = context.get(radiusX);
            var spawnRadiusY = context.get(radiusY);
            var particleType = context.get(particle);
            var durationOfShape = context.get(duration);

            if (sender instanceof PermissionablePlayer player) {

                if (player.hasPermission(Permission.PARTICLE_SHAPE)) {
                    spawnShape.apply(player.getInstance(), player.getPosition(), spawnRadiusX, spawnRadiusY, particleType, durationOfShape);
                } else {
                    sender.sendMessage(Component.text("§4§lNo permission"));
                }
            } else {
                sender.sendMessage(Component.text("§4§lOnly players can use this command"));
            }
        }, twoDimensionalShape, particle, duration, radiusX, radiusY);

    }
}
