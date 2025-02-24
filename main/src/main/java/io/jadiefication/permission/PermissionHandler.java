package io.jadiefication.permission;

import io.jadiefication.permission.sql.PermissionSQLHandler;
import net.minestom.server.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface PermissionHandler extends PermissionSQLHandler {

    Map<Player, List<Permissions>> playerPermissions = new HashMap<>();
    Map<PermissionableGroup, List<Permissions>> groupPermissions = new HashMap<>();

    static void startHandler() {
        Permissions.initialize();
        PermissionSQLHandler.startDatabase();
    }

    static void addPermission(@NotNull Player player, @NotNull Permissions permission) {
        List<Permissions> permissionList = getPermissions(player);
        permissionList.add(permission);
        playerPermissions.replace(player, permissionList);
    }

    static void addPermission(@NotNull PermissionableGroup group, @NotNull Permissions permission) {
        List<Permissions> permissionList = getPermissions(group);
        permissionList.add(permission);
        groupPermissions.replace(group, permissionList);
    }

    static void removePermission(@NotNull Player player, @NotNull Permissions permission) {
        List<Permissions> permissionList = getPermissions(player);
        permissionList.remove(permission);
        playerPermissions.replace(player, permissionList);
    }

    static void removePermission(@NotNull PermissionableGroup group, @NotNull Permissions permission) {
        List<Permissions> permissionList = getPermissions(group);
        permissionList.remove(permission);
        groupPermissions.replace(group, permissionList);
    }

    static List<Permissions> getPermissions(@NotNull Player player) {
        return playerPermissions.get(player);
    }

    static List<Permissions> getPermissions(@NotNull PermissionableGroup group) {
        return groupPermissions.get(group);
    }

    static void setPermissions(@NotNull Player player, @NotNull List<Permissions> permissionList) {
        playerPermissions.replace(player, permissionList);
    }

    static void setPermissions(@NotNull PermissionableGroup group, @NotNull List<Permissions> permissionList) {
        groupPermissions.replace(group, permissionList);
    }
}