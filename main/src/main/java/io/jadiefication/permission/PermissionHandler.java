package io.jadiefication.permission;

import io.jadiefication.permission.sql.PermissionSQLHandler;
import net.minestom.server.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public interface PermissionHandler extends PermissionSQLHandler {

    Map<Player, Set<Permissions>> playerPermissions = new HashMap<>();
    Map<PermissionableGroup, Set<Permissions>> groupPermissions = new HashMap<>();

    static void startHandler() {
        PermissionSQLHandler.startDatabase();
    }

    static void addPermission(@NotNull Player player, @NotNull Permissions permission) {
        Set<Permissions> permissionList = getPermissions(player);
        if (permissionList == null) {
            Set<Permissions> newPermissions = new HashSet<>();
            newPermissions.add(permission);
            playerPermissions.put(player, newPermissions);
        } else {
            permissionList.add(permission);
            playerPermissions.replace(player, permissionList);
        }
    }

    static void addPermission(@NotNull PermissionableGroup group, @NotNull Permissions permission) {
        Set<Permissions> permissionList = getPermissions(group);
        if (permissionList == null) {
            Set<Permissions> newPermissions = new HashSet<>();
            newPermissions.add(permission);
            groupPermissions.put(group, newPermissions);
        } else {
            permissionList.add(permission);
            groupPermissions.replace(group, permissionList);
        }
    }

    static void removePermission(@NotNull Player player, @NotNull Permissions permission) {
        Set<Permissions> permissionList = getPermissions(player);
        if (permissionList != null) {
            permissionList.remove(permission);
            playerPermissions.replace(player, permissionList);
        }
    }

    static void removePermission(@NotNull PermissionableGroup group, @NotNull Permissions permission) {
        Set<Permissions> permissionList = getPermissions(group);
        if (permissionList != null) {
            permissionList.remove(permission);
            groupPermissions.replace(group, permissionList);
        }
    }

    static Set<Permissions> getPermissions(@NotNull Player player) {
        return playerPermissions.get(player);
    }

    static Set<Permissions> getPermissions(@NotNull PermissionableGroup group) {
        return groupPermissions.get(group);
    }

    static void setPermissions(@NotNull Player player, @NotNull Set<Permissions> permissionList) {
        playerPermissions.replace(player, permissionList);
    }

    static void setPermissions(@NotNull PermissionableGroup group, @NotNull Set<Permissions> permissionList) {
        groupPermissions.replace(group, permissionList);
    }
}