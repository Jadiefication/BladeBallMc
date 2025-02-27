package io.jadiefication.core.game.start;

import io.jadiefication.permission.PermissionablePlayer;
import net.minestom.server.entity.Player;
import net.minestom.server.potion.Potion;
import net.minestom.server.potion.PotionEffect;

import java.util.*;

public class Match {

    private final List<PermissionablePlayer> players = new ArrayList<>();

    public Match(Collection<Player> players) {
        this.players.addAll(players.stream().map(player -> ((PermissionablePlayer) player)).toList());
        this.players.forEach(player -> {
            player.addEffect(new Potion(PotionEffect.GLOWING, 1, Integer.MAX_VALUE));
        });
    }

    public List<PermissionablePlayer> getPlayers() {
        return this.players;
    }

    public void removePlayer(Player player) {
        this.players.remove((PermissionablePlayer) player);
    }

    public Optional<Player> isLast() {
        return this.players.size() == 1 ? Optional.of(this.players.getFirst()) : Optional.empty();
    }
}
