package io.jadiefication.permission.sql;

import io.jadiefication.Nimoh;
import io.jadiefication.util.data.player.PlayerDataHandler;
import io.jadiefication.permission.PermissionHandler;
import io.jadiefication.permission.PermissionableGroup;
import io.jadiefication.permission.PermissionablePlayer;
import io.jadiefication.permission.Permissions;
import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public interface PermissionSQLHandler {

    static void startDatabase() {
        try {
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

            try (Statement statement = Nimoh.connection.createStatement()) {
                statement.execute(createPlayerPermissionsTable);
                statement.execute(createGroupPermissionsTable);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    static void getPermissions(@NotNull PermissionablePlayer player) {
        Set<Permissions> permissions = new HashSet<>();
        try {
            String query = "SELECT permission FROM player_permissions WHERE player_uuid = ?";
            PreparedStatement statement = Nimoh.connection.prepareStatement(query);
            statement.setString(1, String.valueOf(player.getUuid()));

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    try {
                        permissions.add(Permissions.valueOf(resultSet.getString("permissions")));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        PermissionHandler.playerPermissions.replace(player, permissions);
    }

    static void getPermissions(@NotNull PermissionableGroup group) {
        Set<Permissions> permissions = new HashSet<>();
        try {
            String query = "SELECT permission FROM group_permissions WHERE group_name = ?";
            PreparedStatement statement = Nimoh.connection.prepareStatement(query);
            statement.setString(1, ((TextComponent) group.getName()).content());

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    try {
                        permissions.add(Permissions.valueOf(resultSet.getString("permissions")));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        PermissionHandler.groupPermissions.replace(group, permissions);
    }

    static void setPermissions(@NotNull PermissionablePlayer player) {
        try {
            // First delete any existing permissions for this player
            String deleteQuery = "DELETE FROM player_permissions WHERE player_uuid = ?";
            try (PreparedStatement deleteStatement = Nimoh.connection.prepareStatement(deleteQuery)) {
                deleteStatement.setString(1, String.valueOf(player.getUuid()));
                deleteStatement.executeUpdate();
            }

            // Then insert the new permissions
            String insertQuery = "INSERT INTO player_permissions (player_uuid, permission) VALUES (?, ?)";
            try {
                PermissionHandler.playerPermissions.get(player).forEach(permissions -> {
                    try {
                        PreparedStatement statement = Nimoh.connection.prepareStatement(insertQuery);
                        statement.setString(1, String.valueOf(player.getUuid()));
                        statement.setString(2, permissions.name());
                        statement.executeUpdate();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });
            } catch (NullPointerException ignored) {
                // No permissions to add
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    static void setPermissions(@NotNull PermissionableGroup group) {
        try {
            // First delete any existing permissions for this player
            String deleteQuery = "DELETE FROM group_permissions WHERE group_name = ?";
            try (PreparedStatement deleteStatement = Nimoh.connection.prepareStatement(deleteQuery)) {
                deleteStatement.setString(1, ((TextComponent) group.getName()).content());
                deleteStatement.executeUpdate();
            }

            // Then insert the new permissions
            String insertQuery = "INSERT INTO group_permissions (group_name, permission) VALUES (?, ?)";
            try {
                PermissionHandler.groupPermissions.get(group).forEach(permissions -> {
                    try {
                        PreparedStatement statement = Nimoh.connection.prepareStatement(insertQuery);
                        statement.setString(1, ((TextComponent) group.getName()).content());
                        statement.setString(2, permissions.name());
                        statement.executeUpdate();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });
            } catch (NullPointerException ignored) {
                // No permissions to add
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    static void removePermission(PermissionablePlayer player, Permissions permission) {
        try {
            String query = "DELETE FROM player_permissions WHERE player_uuid = ? AND permission = ?";
            PreparedStatement statement = Nimoh.connection.prepareStatement(query);
            statement.setString(1, String.valueOf(player.getUuid()));
            statement.setString(2, permission.name());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    static void removePermission(PermissionableGroup group, Permissions permission) {
        try {
            String query = "DELETE FROM group_permissions WHERE group_name = ? AND permission = ?";
            PreparedStatement statement = Nimoh.connection.prepareStatement(query);
            statement.setString(1, ((TextComponent) group.getName()).content());
            statement.setString(2, permission.name());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    static void addPermission(PermissionablePlayer player, Permissions permission) {
        try {
            String query = "INSERT INTO player_permissions (player_uuid, permission) VALUES (?, ?)";
            PreparedStatement statement = Nimoh.connection.prepareStatement(query);
            statement.setString(1, String.valueOf(player.getUuid()));
            statement.setString(2, permission.name());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    static void addPermission(PermissionableGroup group, Permissions permission) {
        try {
            String query = "INSERT INTO group_permissions (group_name, permission) VALUES (?, ?)";
            PreparedStatement statement = Nimoh.connection.prepareStatement(query);
            statement.setString(1, ((TextComponent) group.getName()).content());
            statement.setString(2, permission.name());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
