package io.jadiefication.commands.permission;

import io.jadiefication.commands.Action;
import io.jadiefication.commands.CommandLogic;
import io.jadiefication.commands.arguments.CustomArgumentTypes;
import io.jadiefication.commands.timecommand.Time;
import io.jadiefication.core.data.player.UUIDFetcher;
import io.jadiefication.permission.PermissionHandler;
import io.jadiefication.permission.PermissionablePlayer;
import io.jadiefication.permission.Permissions;
import io.jadiefication.permission.sql.PermissionSQLHandler;
import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.Argument;
import net.minestom.server.command.builder.arguments.ArgumentEnum;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.network.packet.server.SendablePacket;
import net.minestom.server.network.player.GameProfile;
import net.minestom.server.network.player.PlayerConnection;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.RecordComponent;
import java.net.SocketAddress;
import java.util.UUID;

public class PermissionCommand extends Command implements CommandLogic {

    public PermissionCommand() {
        super("permission");

        var action = ArgumentType.Enum("action", PermissionAction.class).setFormat(ArgumentEnum.Format.LOWER_CASED);
        var username = ArgumentType.String("player");
        var permission = CustomArgumentTypes.Record("permission", Permissions.class);

        defaultExecutor(this);

        argumentCallbacks(new Argument[]{action, permission});

        addSyntax((sender, context) -> {
            final PermissionAction doAction = context.get(action);
            final String inputUsername = context.get(username);
            final RecordComponent _permission = context.get(permission);
            if (sender instanceof PermissionablePlayer _player) {

                if (_player.hasPermission(Permissions.getPermission("OP"))) {
                    PermissionablePlayer player = (PermissionablePlayer) MinecraftServer.getConnectionManager().findOnlinePlayer(inputUsername);
                    if (player == null) {
                        UUID playerUUID = UUIDFetcher.getUUID(inputUsername);
                        assert playerUUID != null;
                        PermissionablePlayer offlinePlayer = ((PermissionablePlayer) MinecraftServer.getConnectionManager().createPlayer(new PlayerConnection() {
                            @Override
                            public void sendPacket(@NotNull SendablePacket packet) {
                                throw new UnsupportedOperationException("Fake player");
                            }

                            @Override
                            public @NotNull SocketAddress getRemoteAddress() {
                                return null;
                            }
                        }, new GameProfile(playerUUID, inputUsername)));

                        if (doAction.equals(PermissionAction.ADD)) {
                            PermissionSQLHandler.addPermission(offlinePlayer, Permissions.getPermission(_permission.getName()));
                        } else if (doAction.equals(PermissionAction.DELETE)) {
                            PermissionSQLHandler.removePermission(offlinePlayer, Permissions.getPermission(_permission.getName()));
                        }
                    } else {
                        if (doAction.equals(PermissionAction.ADD)) {
                            PermissionHandler.addPermission(player, Permissions.getPermission(_permission.getName()));
                        } else if (doAction.equals(PermissionAction.DELETE)) {
                            PermissionHandler.removePermission(player, Permissions.getPermission(_permission.getName()));
                        }
                    }
                }
            } else {
                sender.sendMessage(Component.text("§4§lOnly players can use this command"));
            }
        }, action, username, permission);
    }
}
