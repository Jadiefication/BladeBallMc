package io.jadiefication.core.start.team;

import io.jadiefication.core.Handler;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.InstanceContainer;
import net.jadiefication.stream.StreamExpander;

import java.util.List;
import java.util.Set;

public interface TeamHandler extends Handler {

    default void initialize(List<GameTeam> teams) {
        TeamInsides.teams = teams;
    }

    @Override
    default void start(InstanceContainer container) {
        Set<Player> players = container.getPlayers();
        StreamExpander<Player> stream = (StreamExpander<Player>) players.stream();
        stream.forEachIndexed((player, i) -> {
            if (i % 2 == 0) {

            } else {

            }
        });
    }

    @Override
    default void update(InstanceContainer container) {

    }

    class TeamInsides {

        private static List<GameTeam> teams;
    }
}
