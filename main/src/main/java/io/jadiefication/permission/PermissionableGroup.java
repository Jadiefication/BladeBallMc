package io.jadiefication.permission;

import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PermissionableGroup {

    private final Component name;
    private final Set<Player> players;
    private final Set<Permissions> permissions;
    private final static Map<PermissionableGroup, Set<Player>> groupPlayers = new HashMap<>();

    public PermissionableGroup(Component name, Set<Player> players, Set<Permissions> permissions) {
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

    public void setPlayers(Set<Player> players) {
        this.players.clear();
        this.players.addAll(players);
        groupPlayers.replace(this, players);
    }

    public static boolean isPlayerInGroup(Player player, PermissionableGroup group) {
        return groupPlayers.get(group).contains(player);
    }

    public static Set<Player> getPlayers(PermissionableGroup group) {
        return groupPlayers.get(group);
    }

    public Set<Player> getPlayers() {
        return players;
    }

    public Component getName() {
        return name;
    }

}
