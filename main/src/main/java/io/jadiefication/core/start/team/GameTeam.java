package io.jadiefication.core.start.team;

import net.minestom.server.entity.Player;

import java.util.List;

public class GameTeam {

    private List<Player> players;
    private String name;

    public GameTeam(List<Player> players, String name) {
        this.players = players;
        this.name = name;
    }

    public GameTeam(String name) {
        this.name = name;
        this.players = null;
    }

    public void addPlayer(Player player) {
        if (players == null) {
            players = List.of(player);
        } else {
            players.add(player);
        }
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }
}
