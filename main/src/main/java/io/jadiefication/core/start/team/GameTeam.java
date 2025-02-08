package io.jadiefication.core.start.team;

import io.jadiefication.permission.PermissionablePlayer;
import net.jadiefication.map.HashMapExtender;
import net.jadiefication.map.MapExtender;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;

import java.util.List;

public class GameTeam {

    private List<PermissionablePlayer> players;
    private String name;
    private Component tag;
    private static final MapExtender<GameTeam, List<PermissionablePlayer>> playersToTeam = new HashMapExtender<>();

    public GameTeam(List<PermissionablePlayer> players, String name, Component tag) {
        this.players = players;
        this.name = name;
        this.tag = tag;
    }

    public GameTeam(String name, Component tag) {
        this.name = name;
        this.players = null;
        this.tag = tag;
    }

    public void addPlayer(PermissionablePlayer player) {
        if (this.players == null) {
            this.setPlayers(List.of(player));
        } else {
            this.players.add(player);
        }
    }

    public List<PermissionablePlayer> getPlayers() {
        return this.players;
    }

    public void setPlayers(List<PermissionablePlayer> players) {
        this.players = players;
    }

    public void resetPlayerList() {
        this.players = null;
    }

    public static boolean areOnSameTeam(Player player1, Player player2) {
        return playersToTeam.hasListValue(player1) && playersToTeam.hasListValue(player2);
    }

    public static GameTeam getTeam(Player player) {
        return playersToTeam.getKeyByListValue(player);
    }

    public void removePlayer(Player player) {
        this.players.remove(player);
        playersToTeam.replace(this, this.players);
    }

    public boolean isEmpty() {
        return this.players.isEmpty();
    }
}
