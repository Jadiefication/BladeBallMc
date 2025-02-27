package io.jadiefication.core.game.start;

import io.jadiefication.permission.PermissionablePlayer;
import net.minestom.server.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Match {

    private final List<PermissionablePlayer> players = new ArrayList<>();

    public Match(List<Player> players) {
        this.players.addAll(players.stream().map(player -> ((PermissionablePlayer) player)).toList());
    }

    public void removePlayer(Player player) {
        this.players.remove((PermissionablePlayer) player);
    }

    public Optional<Player> isLast() {
        return this.players.size() == 1 ? Optional.of(this.players.getFirst()) : Optional.empty();
    }
}
