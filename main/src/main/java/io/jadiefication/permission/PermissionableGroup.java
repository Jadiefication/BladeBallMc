package io.jadiefication.permission;

import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PermissionableGroup {

    private final Component name;
    private final List<Player> players;
    private final List<Permission> permissions;
    private final static Map<PermissionableGroup, List<Player>> groupPlayers = new HashMap<>();

    public PermissionableGroup(Component name, List<Player> players, List<Permission> permissions) {
        this.name = name;
        this.players = players;
        this.permissions = permissions;
        groupPlayers.put(this, players);
    }

    public void addPlayer(Player player) {
        players.add(player);
        groupPlayers.replace(this, players);
    }

    public void removePlayer(Player player) {
        players.remove(player);
        groupPlayers.replace(this, players);
    }

    public void setPlayers(List<Player> players) {
        this.players.clear();
        this.players.addAll(players);
        groupPlayers.replace(this, players);
    }

    public void addPermission(Permission permission) {
        PermissionHandler.addPermission(this, permission);
    }

    public void setPermissions(List<Permission> permissions) {
        PermissionHandler.setPermissions(this, permissions);
    }

    public void removePermission(Permission permission) {
        PermissionHandler.removePermission(this, permission);
    }

    public static boolean isPlayerInGroup(Player player, PermissionableGroup group) {
        return groupPlayers.get(group).contains(player);
    }

    public static List<Player> getPlayers(PermissionableGroup group) {
        return groupPlayers.get(group);
    }

    public List<Player> getPlayers() {
        return players;
    }

    public Component getName() {
        return name;
    }

}
