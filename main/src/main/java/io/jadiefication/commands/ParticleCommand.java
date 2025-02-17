package io.jadiefication.commands;

import io.jadiefication.particlegenerator.ParticleBuilder;
import io.jadiefication.particlegenerator.packets.PacketReceiver;
import io.jadiefication.particlegenerator.particle.ParticleShapes;
import io.jadiefication.permission.Permission;
import io.jadiefication.permission.PermissionablePlayer;
import net.kyori.adventure.text.Component;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.Argument;
import net.minestom.server.command.builder.arguments.ArgumentEnum;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.arguments.minecraft.registry.ArgumentParticle;
import net.minestom.server.command.builder.arguments.number.ArgumentDouble;
import net.minestom.server.command.builder.arguments.number.ArgumentFloat;

public class ParticleCommand extends Command implements CommandLogic {

    private final ArgumentEnum<ParticleShapes> shape = ArgumentType.Enum("shape", ParticleShapes.class).setFormat(ArgumentEnum.Format.LOWER_CASED);
    private final ArgumentDouble radiusX = ArgumentType.Double("radiusX");
    private final ArgumentDouble radiusY = ArgumentType.Double("radiusY");
    private final ArgumentDouble radiusZ = ArgumentType.Double("radiusZ");
    private final ArgumentFloat duration = ArgumentType.Float("duration");
    private final ArgumentParticle particle = ArgumentType.Particle("particle");

    public ParticleCommand() {
        super("particle");

        defaultExecutor(this);

        argumentCallbacks(new Argument[]{shape, radiusX, radiusY, radiusZ, duration, particle});

        addSyntax((sender, context) -> {
            var spawnShape = context.get(shape);
            if (spawnShape.isThree) {
                executeCommand(sender, context, true);
            }
        }, shape, particle, duration, radiusX, radiusY, radiusZ);

        // 2D shapes syntax
        addSyntax((sender, context) -> {
            var spawnShape = context.get(shape);
            if (!spawnShape.isThree) {
                executeCommand(sender, context, false);
            }
        }, shape, particle, duration, radiusX, radiusY);
    }

    private void executeCommand(CommandSender sender, CommandContext context, boolean is3D) {
        var spawnShape = context.get(shape);
        var spawnRadiusX = context.get(radiusX);
        var spawnRadiusY = context.get(radiusY);
        var spawnRadiusZ = is3D ? context.get(radiusZ) : 0.0;
        var particleType = context.get(particle);
        var durationOfShape = context.get(duration);

        if (sender instanceof PermissionablePlayer player) {
            if (player.hasPermission(Permission.PARTICLE_SHAPE)) {
                if (is3D) ParticleBuilder.build(spawnShape, player.getPosition(), spawnRadiusX, spawnRadiusY, spawnRadiusZ, new PacketReceiver(player.getInstance(), particleType), durationOfShape);
                else ParticleBuilder.build(spawnShape, player.getPosition(), spawnRadiusX, spawnRadiusY, new PacketReceiver(player.getInstance(), particleType), durationOfShape);
            } else {
                sender.sendMessage(Component.text("§4§lNo permission"));
            }
        } else {
            sender.sendMessage(Component.text("§4§lOnly players can use this command"));
        }
    }

}
