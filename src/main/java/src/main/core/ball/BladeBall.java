package src.main.core.ball;

import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.damage.Damage;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.block.Block;
import net.minestom.server.particle.Particle;
import net.minestom.server.timer.Scheduler;
import net.minestom.server.timer.Task;
import net.minestom.server.timer.TaskSchedule;
import src.main.core.ball.entity.BallEntity;
import src.main.particlegenerator.ParticleGenerator;

public non-sealed class BladeBall implements BallHandler {

    public static BallEntity entity;
    private Player homedUponPlayer;
    private boolean hasPlayer = false;
    private int hitWall;

    @Override
    public void update(InstanceContainer container) {
        int dt = BallState.dt;

        BallState.dtCounter.cancel();
        BallState.dt = 0;

        if (!hasPlayer) {
            homedUponPlayer = BallState.firstTarget ? BallState.findFirstTarget(container) : findTarget(container);
            BallState.firstTarget = false;
            hasPlayer = true;
        }

        if (homedUponPlayer != null) {
            try {
                Vec movementVec = getMovementVec(homedUponPlayer);
                if (!BallState.tasks.isEmpty()) {
                    if (container.getBlock(BallState.ballPosition) != Block.AIR) {
                        if (hitWall != 60) {
                            // Ball hit a wall, apply bounce-back effect
                            BallState.ballPosition = BallState.ballPosition.sub(movementVec.mul(0.2)); // Move back slightly

                            // Invert the movement vector for a bounce effect
                            movementVec = movementVec.mul(-0.8); // Adjust bounce strength
                            hitWall++;
                        }
                    }
                    doHoming(movementVec, container);
                }
                if (BallState.ballPosition.distanceSquared(homedUponPlayer.getPosition()) < 0.25) {
                    homedUponPlayer.setGameMode(GameMode.SPECTATOR);
                    ParticleGenerator.spawnSphereParticles(container, homedUponPlayer.getPosition(), 0.5, 0.5, 0.5, Particle.ITEM_SNOWBALL, 1.0);
                    homedUponPlayer = null;
                    BallState.tasks.forEach(tasks -> tasks.forEach(Task::cancel));
                    BallState.tasks.clear();
                    entity.remove();
                    start(container);
                }
            } catch (NullPointerException ignored) {

            }
        }
    }

    private static void doHoming(Vec movementVec, InstanceContainer container) {
        entity.remove();
        BallState.ballPosition = BallState.ballPosition.add(movementVec);

        BallState.tasks.forEach(tasks -> tasks.forEach(Task::cancel));
        BallState.tasks.clear();
        BallState.tasks = ParticleGenerator.spawnSphereParticles(
                container, BallState.ballPosition, 0.5, 0.5, 0.5, Particle.WAX_ON, Double.POSITIVE_INFINITY
        );
        entity = new BallEntity(BallState.ballPosition, container);
    }

    private static Vec getMovementVec(Player player) {
        Vec directionVec = Vec.fromPoint(player.getPosition().sub(BallState.ballPosition));
        Vec normalizedVec = directionVec.normalize();
        double speedPerBlocks = 0.5;
        return normalizedVec.mul(speedPerBlocks);
    }

    @Override
    public void start(InstanceContainer container) {
        BallState.stayingStill = true;
        BallState.ballPosition = new Pos(0.5, 45.0, 0.5);
        hasPlayer = false;

        BallState.tasks = ParticleGenerator.spawnSphereParticles(container, BallState.ballPosition, 0.5, 0.5, 0.5, Particle.WAX_OFF, Double.POSITIVE_INFINITY);
        entity = new BallEntity(BallState.ballPosition, container);

        BallState.isActive = true;
        BallState.firstTarget = true;
    }
}
