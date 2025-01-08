package src.main.core.ball;

import net.minestom.server.entity.Player;
import net.minestom.server.instance.InstanceContainer;

public non-sealed class BladeBall implements BallHandler {

    @Override
    public void update(InstanceContainer container) {
        int dt = BallState.dt;
        BallState.dtCounter.cancel();

        if (BallState.firstTarget) {
            Player player = BallState.findFirstTarget(container);
        }
    }
}
