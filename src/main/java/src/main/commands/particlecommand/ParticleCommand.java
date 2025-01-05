package src.main.commands.particlecommand;

import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import src.main.particlegenerator.ParticleShapes;
import src.main.permission.Permission;
import src.main.permission.PermissionablePlayer;

public class ParticleCommand extends Command {

    public ParticleCommand() {
        super("particle");

        var shape = ArgumentType.Enum("shape", ParticleShapes.class);
        var radiusX = ArgumentType.Double("radiusX");
        var radiusY = ArgumentType.Double("radiusY");

        setDefaultExecutor((sender, context) -> {
            sender.sendMessage("§4§lNo arguments given");
        });

        shape.setCallback((sender, e) -> {
            if (!e.getInput().equalsIgnoreCase("octagon")) {
                sender.sendMessage("§4§lInvalid shape");
            }
        });

        radiusX.setCallback((sender, e) -> {
            if (Double.parseDouble(e.getInput()) < 0) {
                sender.sendMessage("§4§lRadius must be positive");
            }
        });

        addSyntax((sender, context) -> {
            var spawnShape = context.get(shape);
            var spawnRadiusX = context.get(radiusX);
            var spawnRadiusY = context.get(radiusY);
            PermissionablePlayer player = (PermissionablePlayer) sender;

            if (spawnShape == null || spawnRadiusX == null) {
                sender.sendMessage("§4§lNo shape/radius given");
            } else {
                if (player.hasPermission(Permission.PARTICLE_SHAPE)) {
                    spawnShape.apply(player.getInstance(), player.getPosition(), spawnRadiusX, spawnRadiusY);
                } else {
                    sender.sendMessage("§4§lNo permission");
                }
            }
        }, shape, radiusX, radiusY);

    }
}
