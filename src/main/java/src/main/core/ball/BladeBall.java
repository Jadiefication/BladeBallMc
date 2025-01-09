package src.main.core.ball;

import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.damage.Damage;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.particle.Particle;
import net.minestom.server.timer.Scheduler;
import net.minestom.server.timer.Task;
import net.minestom.server.timer.TaskSchedule;
import src.main.particlegenerator.ParticleGenerator;

public non-sealed class BladeBall implements BallHandler {

    @Override
    public void update(InstanceContainer container) {
        int dt = BallState.dt;

        BallState.dtCounter.cancel();
        BallState.dt = 0;

        Player player = BallState.findFirstTarget(container);
        if (player != null) {
            try {
                if (!BallState.tasks.isEmpty()) {

                    Vec directionVec = Vec.fromPoint(player.getPosition().sub(BallState.ballPosition));
                    Vec normalizedVec = directionVec.normalize();
                    double speedPerBlocks = 0.5;
                    Vec movementVec = normalizedVec.mul(speedPerBlocks);
                    BallState.ballPosition = BallState.ballPosition.add(movementVec);

                    BallState.tasks.forEach(tasks -> tasks.forEach(Task::cancel));
                    BallState.tasks = ParticleGenerator.spawnSphereParticles(
                            container, BallState.ballPosition, 0.5, 0.5, 0.5, Particle.WAX_ON, Double.POSITIVE_INFINITY
                    );
                }
                if (BallState.ballPosition.distance(player.getPosition()) < 0.5) {
                    player.setGameMode(GameMode.SPECTATOR);
                    ParticleGenerator.spawnSphereParticles(container, player.getPosition(), 0.5, 0.5, 0.5, Particle.ITEM_SNOWBALL, 1.0);
                }
            } catch (NullPointerException ignored) {

            }
        }
    }

    @Override
    public void start(InstanceContainer container) {
        BallState.isActive = false;
        BallState.stayingStill = true;

        BallState.tasks = ParticleGenerator.spawnSphereParticles(container, BallState.ballPosition, 0.5, 0.5, 0.5, Particle.WAX_OFF, Double.POSITIVE_INFINITY);

        BallState.isActive = true;
        BallState.firstTarget = true;
    }
}
