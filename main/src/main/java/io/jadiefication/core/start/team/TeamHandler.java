package io.jadiefication.core.start.team;

import io.jadiefication.core.Handler;
import io.jadiefication.permission.PermissionablePlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.metadata.PlayerMeta;
import net.minestom.server.instance.InstanceContainer;
import net.jadiefication.stream.StreamExpander;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface TeamHandler extends Handler {

    TeamHandler handler = new TeamHandler() {};

    static TeamHandler getInstance() {
        return handler;
    }

    static void initialize() {
        TeamInsides.blue = new GameTeam("blue", Component.text("Blue", TextColor.fromHexString("#1aaede")));
        TeamInsides.red = new GameTeam("red", Component.text("Red", TextColor.fromHexString("#de351a")));
    }

    default void start(InstanceContainer container) {
        Set<Player> players = container.getPlayers();
        StreamExpander<Player> stream = (StreamExpander<Player>) players.stream();
        stream.forEachIndexed((player, i) -> {
            PlayerMeta meta = player.getPlayerMeta();
            if (i % 2 == 0) {
                meta.setCustomName(Component.text("Blue | " + player.getName(), TextColor.fromHexString("#1aaede")));
                TeamInsides.blue.addPlayer(((PermissionablePlayer) player));
            } else {
                meta.setCustomName(Component.text("Red | " + player.getName(), TextColor.fromHexString("#de351a")));
                TeamInsides.red.addPlayer(((PermissionablePlayer) player));
            }
        });
    }

    default void reset() {
        TeamInsides.blue.resetPlayerList();
        TeamInsides.red.resetPlayerList();
    }

    default Optional<GameTeam> isEmpty() {
        Optional<GameTeam> optional = Optional.empty();
        if (TeamInsides.red.isEmpty()) {
            optional = Optional.of(TeamInsides.red);
        } else if (TeamInsides.blue.isEmpty()) {
            optional = Optional.of(TeamInsides.blue);
        }

        return optional;
    }

    default GameTeam getOpposingTeam(GameTeam team) throws IncorrectTeamException {
        if (team.equals(TeamInsides.blue)) {
            return TeamInsides.red;
        } else if (team.equals(TeamInsides.red)) {
            return TeamInsides.blue;
        } else {
            throw new IncorrectTeamException();
        }
    }

    @Override
    default void update(InstanceContainer container) {

    }

    class TeamInsides {

        private static GameTeam blue;
        private static GameTeam red;
    }
}
