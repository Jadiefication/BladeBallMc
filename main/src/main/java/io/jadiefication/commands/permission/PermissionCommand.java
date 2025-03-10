package io.jadiefication.commands.permission;

import io.jadiefication.Nimoh;
import io.jadiefication.commands.CommandLogic;
import io.jadiefication.util.data.player.UUIDFetcher;
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

import java.net.SocketAddress;
import java.util.UUID;

public class PermissionCommand extends Command implements CommandLogic {

    public PermissionCommand() {
        super("permission");

        var action = ArgumentType.Enum("action", PermissionAction.class).setFormat(ArgumentEnum.Format.LOWER_CASED);
        var username = ArgumentType.String("player");
        var permission = ArgumentType.Enum("permission", Permissions.class).setFormat(ArgumentEnum.Format.LOWER_CASED);

        defaultExecutor(this);

        argumentCallbacks(new Argument[]{action, permission, username});

        addSyntax((sender, context) -> {
            final PermissionAction doAction = context.get(action);
            final String inputUsername = context.get(username);
            final Permissions _permission = context.get(permission);
            if (sender instanceof PermissionablePlayer _player) {

                if (_player.hasPermission(Permissions.OP)) {
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
                            PermissionSQLHandler.addPermission(offlinePlayer, _permission);
                        } else if (doAction.equals(PermissionAction.DELETE)) {
                            PermissionSQLHandler.removePermission(offlinePlayer, _permission);
                        }
                    } else {
                        if (doAction.equals(PermissionAction.ADD)) {
                            PermissionHandler.addPermission(player, _permission);
                        } else if (doAction.equals(PermissionAction.DELETE)) {
                            PermissionHandler.removePermission(player, _permission);
                        }
                    }
                } else {
                    sender.sendMessage(Component.text("§4§lNo permission"));
                }
            } else {
                sender.sendMessage(Component.text("§4§lOnly players can use this command"));
            }
        }, action, username, permission);
    }
}
