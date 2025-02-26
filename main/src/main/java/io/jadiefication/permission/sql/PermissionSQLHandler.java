package io.jadiefication.permission.sql;

import io.jadiefication.core.data.player.PlayerDataHandler;
import io.jadiefication.permission.PermissionHandler;
import io.jadiefication.permission.PermissionableGroup;
import io.jadiefication.permission.PermissionablePlayer;
import io.jadiefication.permission.Permissions;
import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public interface PermissionSQLHandler {

    static void startDatabase() {
        try (Connection connection = PlayerDataHandler.Config.dataSource.getConnection()) {
            // Create the tables if they do not exist
            String createPlayerPermissionsTable = """
                CREATE TABLE IF NOT EXISTS player_permissions (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    player_uuid TEXT NOT NULL,
                    permission TEXT NOT NULL
                )
            """;

            String createGroupPermissionsTable = """
                CREATE TABLE IF NOT EXISTS group_permissions (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    group_name TEXT NOT NULL,
                    permission TEXT NOT NULL
                )
            """;

            try (Statement statement = connection.createStatement()) {
                statement.execute(createPlayerPermissionsTable);
                statement.execute(createGroupPermissionsTable);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    static void getPermissions(@NotNull PermissionablePlayer player) {
        List<Permissions> permissions = new ArrayList<>();
        try (Connection connection = PlayerDataHandler.Config.dataSource.getConnection()) {
            String query = "SELECT permission FROM player_permissions WHERE player_uuid = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, String.valueOf(player.getUuid()));

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    permissions.add(Permissions.getPermission(resultSet.getString("player_permissions")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        PermissionHandler.playerPermissions.replace(player, permissions);
    }

    static void getPermissions(@NotNull PermissionableGroup group) {
        List<Permissions> permissions = new ArrayList<>();
        try (Connection connection = PlayerDataHandler.Config.dataSource.getConnection()) {
            String query = "SELECT permission FROM group_permissions WHERE group_name = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, ((TextComponent) group.getName()).content());

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    permissions.add(Permissions.getPermission(resultSet.getString("group_permissions")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        PermissionHandler.groupPermissions.replace(group, permissions);
    }

    static void setPermissions(@NotNull PermissionablePlayer player) {
        try (Connection connection = PlayerDataHandler.Config.dataSource.getConnection()) {
            String query = "REPLACE INTO player_permissions (player_uuid, permission) VALUES (?, ?)";
            try {
                PermissionHandler.playerPermissions.get(player).forEach(permissions -> {
                    try {
                        PreparedStatement statement = connection.prepareStatement(query);
                        statement.setString(1, String.valueOf(player.getUuid()));
                        statement.setString(2, permissions.name());
                        statement.executeUpdate();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });
            } catch (NullPointerException ignored) {

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    static void setPermissions(@NotNull PermissionableGroup group) {
        try (Connection connection = PlayerDataHandler.Config.dataSource.getConnection()) {
            String query = "REPLACE INTO group_permissions (group_name, permission) VALUES (?, ?)";
            PermissionHandler.groupPermissions.get(group).forEach(permissions -> {
                try {
                    PreparedStatement statement = connection.prepareStatement(query);
                    statement.setString(1, ((TextComponent) group.getName()).content());
                    statement.setString(2, permissions.name());
                    statement.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    static void removePermission(PermissionablePlayer player, Permissions permission) {
        try (Connection connection = PlayerDataHandler.Config.dataSource.getConnection()) {
            String query = "DELETE FROM player_permissions WHERE player_uuid = ? AND permission = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, String.valueOf(player.getUuid()));
            statement.setString(2, permission.name());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    static void removePermission(PermissionableGroup group, Permissions permission) {
        try (Connection connection = PlayerDataHandler.Config.dataSource.getConnection()) {
            String query = "DELETE FROM group_permissions WHERE group_name = ? AND permission = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, ((TextComponent) group.getName()).content());
            statement.setString(2, permission.name());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    static void addPermission(PermissionablePlayer player, Permissions permission) {
        try (Connection connection = PlayerDataHandler.Config.dataSource.getConnection()) {
            String query = "INSERT INTO player_permissions (player_uuid, permission) VALUES (?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, String.valueOf(player.getUuid()));
            statement.setString(2, permission.name());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    static void addPermission(PermissionableGroup group, Permissions permission) {
        try (Connection connection = PlayerDataHandler.Config.dataSource.getConnection()) {
            String query = "INSERT INTO group_permissions (group_name, permission) VALUES (?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, ((TextComponent) group.getName()).content());
            statement.setString(2, permission.name());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
