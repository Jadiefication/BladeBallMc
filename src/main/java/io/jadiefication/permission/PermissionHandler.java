package io.jadiefication.permission;

import io.ebean.Database;
import io.ebean.DatabaseFactory;
import io.ebean.config.DatabaseConfig;
import net.kyori.adventure.text.TextComponent;
import net.minestom.server.entity.Player;
import org.jetbrains.annotations.NotNull;
import io.jadiefication.permission.database.player.PlayerPermissionEntry;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class PermissionHandler {

    private final static Map<Player, List<Permission>> playerPermissions = new HashMap<>();
    private final static Map<PermissionableGroup, List<Permission>> groupPermissions = new HashMap<>();
    public static Database permissionDatabase;

    public static void startHandler() {
        File file = new File("permissions.db");
            try {
                boolean ignored = file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        startDatabase();
    }

    private static void startDatabase() {
        DatabaseConfig permissionConfig = new DatabaseConfig();
        permissionConfig.setName("permissions");

        permissionDatabase = DatabaseFactory.create(permissionConfig);
    }

    public static void addPermission(@NotNull Player player, @NotNull Permission permission) {
        //PlayerPermissionEntry playerEntry =

        /*List<Permission> permissionList = getPermissions(player);
        permissionList.add(permission);
        playerPermissions.replace(player, permissionList);*/
    }

    public static void addPermission(@NotNull PermissionableGroup group, @NotNull Permission permission) {
        //doSQL("INSERT INTO group_permissions (group_name, permission) VALUES (?, ?)", group, permission);

        /*List<Permission> permissionList = getPermissions(group);
        permissionList.add(permission);
        groupPermissions.replace(group, permissionList);*/
    }

    public static void removePermission(@NotNull Player player, @NotNull Permission permission) {
        //doSQL("DELETE FROM player_permissions WHERE player_uuid = ? AND permission = ?", player, permission);

        /*List<Permission> permissionList = getPermissions(player);
        permissionList.remove(permission);
        playerPermissions.replace(player, permissionList);*/
    }

    public static void removePermission(@NotNull PermissionableGroup group, @NotNull Permission permission) {
        //doSQL("DELETE FROM group_permissions WHERE group_name = ? AND permission = ?", group, permission);

        /*List<Permission> permissionList = getPermissions(group);
        permissionList.remove(permission);
        groupPermissions.replace(group, permissionList);*/
    }

    public static List<Permission> getPermissions(@NotNull Player player) {
        List<Permission> permissions = new ArrayList<>();
        /*try (Connection connection = DriverManager.getConnection(url)) {
            String sql = "SELECT permission FROM player_permissions WHERE player_uuid = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, player.getUuid().toString());
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    permissions.add(Permission.valueOf(resultSet.getString("permission")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }*/

        return permissions;
    }

    public static List<Permission> getPermissions(@NotNull PermissionableGroup group) {
        List<Permission> permissions = new ArrayList<>();
        /*try (Connection connection = DriverManager.getConnection(url)) {
            String sql = "SELECT permission FROM group_permissions WHERE group_name = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, ((TextComponent) group.getName()).content());
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    permissions.add(Permission.valueOf(resultSet.getString("permission")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }*/

        return permissions;
    }

    public static void setPermissions(@NotNull Player player, @NotNull List<Permission> permissionList) {
        /*try (Connection connection = DriverManager.getConnection(url)) {
            String sql = "DELETE FROM player_permissions WHERE player_uuid = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, player.getUuid().toString());
                statement.executeUpdate();
            }
            for (Permission permission : permissionList) {
                sql = "INSERT INTO player_permissions (player_uuid, permission) VALUES (?, ?)";
                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setString(1, player.getUuid().toString());
                    statement.setString(2, permission.name());
                    statement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }*/
        //playerPermissions.replace(player, permissionList);
    }

    public static void setPermissions(@NotNull PermissionableGroup group, @NotNull List<Permission> permissionList) {
        /*try (Connection connection = DriverManager.getConnection(url)) {
            String sql = "DELETE FROM player_permissions WHERE group_name = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, ((TextComponent) group.getName()).content());
                statement.executeUpdate();
            }
            for (Permission permission : permissionList) {
                sql = "INSERT INTO player_permissions (group_name, permission) VALUES (?, ?)";
                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setString(1, ((TextComponent) group.getName()).content());
                    statement.setString(2, permission.name());
                    statement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }*/
        //groupPermissions.replace(group, permissionList);
    }

}
