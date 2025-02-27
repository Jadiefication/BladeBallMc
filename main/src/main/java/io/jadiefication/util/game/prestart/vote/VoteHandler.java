package io.jadiefication.util.game.prestart.vote;

import io.jadiefication.util.Handler;
import net.minestom.server.instance.InstanceContainer;

public interface VoteHandler extends Handler {

    VoteHandler handler = new VoteHandler() {};

    static VoteHandler getInstance() {
        return handler;
    }

    @Override
    default void start(InstanceContainer container) {
        Vote.gamemode = null;
    }

    @Override
    default void update(InstanceContainer container) {

    }

    default void restart() {
        Vote.gamemode = null;
    }

    class Vote {

        public static VoteGamemode gamemode = VoteGamemode.SOLO;
    }
}
