package io.jadiefication.core.ball;

import io.jadiefication.core.ball.entity.BallEntity;
import io.jadiefication.particlegenerator.ParticleGenerator;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.block.Block;
import net.minestom.server.particle.Particle;
import net.minestom.server.potion.Potion;
import net.minestom.server.potion.PotionEffect;
import net.minestom.server.scoreboard.Team;
import net.minestom.server.scoreboard.TeamBuilder;
import net.minestom.server.scoreboard.TeamManager;
import net.minestom.server.timer.Task;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;


/**
 * Scheduler... FUCK YOU
 *
 */
public non-sealed class BladeBall implements BallHandler {

    public static BallEntity entity;
    private static Player homedUponPlayer;
    public boolean hasPlayer = false;
    private int hitWall;
    private static final Object ballPositionLock = new Object();
    private static final Object homedPlayerLock = new Object();
    private static final Object tasksLock = new Object();

    @Override
    public void update(InstanceContainer container) {
        int dt = BallState.dt;

        BallState.dtCounter.cancel();
        BallState.dt = 0;

        if (!hasPlayer) {
            CompletableFuture.supplyAsync(() -> BallState.firstTarget ? BallState.findFirstTarget(container) : findTarget(container)).thenAccept(result -> {
                synchronized (homedPlayerLock) {
                    homedUponPlayer = result;
                }
                BallState.firstTarget = false;
                hasPlayer = true;

            }).exceptionally(e -> {
                e.printStackTrace();
                return null;
            });
            /*homedUponPlayer = BallState.firstTarget ? BallState.findFirstTarget(container) : findTarget(container);
            BallState.firstTarget = false;
            hasPlayer = true;*/
        } else {
            handleHomingLogic(container);
        }

        /*if (homedUponPlayer != null) {
            try {
                CompletableFuture.supplyAsync(() -> getMovementVec(homedUponPlayer)).thenAccept(result -> {
                    boolean shouldBounce;
                    Vec updatedPosition;
                    Vec bounceResult = result;

                    synchronized (ballPositionLock) {
                        // Perform only minimal state updates inside the synchronized block
                        if (container.getBlock(BallState.ballPosition) != Block.AIR && hitWall != 60) {
                            BallState.ballPosition = BallState.ballPosition.sub(result.mul(0.2)); // Move back slightly
                            bounceResult = result.mul(-0.8); // Calculate bounce vector
                            hitWall++;
                            shouldBounce = true; // Use this flag outside the block
                        } else {
                            shouldBounce = false;
                        }
                        updatedPosition = Vec.fromPoint(BallState.ballPosition); // Cache the position safely
                    }

                    if (shouldBounce) {
                        doHoming(bounceResult, container); // Handle bouncing
                    } else {
                        doHoming(result, container); // Normal homing logic
                    }
                }).exceptionally(e -> {
                    e.printStackTrace();
                    return null;
                });
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
                    ParticleGenerator.spawnCircleParticles(container, homedUponPlayer.getPosition(), 0.5, 0.5, Particle.ITEM_SNOWBALL, 1);
                    homedUponPlayer = null;
                    BallState.tasks.forEach(tasks -> {
                        tasks.forEach(Task::cancel);
                        tasks.clear();
                    });
                    BallState.tasks.clear();
                    entity.remove();
                    start(container);
                }
            } catch (NullPointerException ignored) {

            }
        }*/
    }

    private static void doHoming(Vec movementVec, InstanceContainer container) {
        synchronized (ballPositionLock) {
            BallState.ballPosition = BallState.ballPosition.add(movementVec);
        }
        entity.teleport(BallState.ballPosition);
        // Always create new particle tasks after movement
        List<Player> players = new java.util.ArrayList<>(container.getPlayers().stream().toList());
        players.remove(homedUponPlayer);

        synchronized (tasksLock) {
            // Clear existing tasks
            BallState.tasks.forEach(tasks -> {
                tasks.forEach(Task::cancel);
                tasks.clear();
            });
            BallState.tasks.clear();
            BallState.tasks = ParticleGenerator.spawnSphereParticles(
                    container,
                    BallState.ballPosition,
                    0.5, 0.5, 0.5,
                    players.isEmpty() ? Particle.WAX_ON : Particle.WAX_OFF,
                    1.0
            );
        }
    }


    private static Vec getMovementVec(Player player) {
        try {
            return CompletableFuture.supplyAsync(() -> {
                synchronized (player) {
                    Vec directionVec = Vec.fromPoint(player.getPosition().sub(BallState.ballPosition));
                    Vec normalizedVec = directionVec.normalize();
                    double speedPerBlocks = 0.5;

                    return normalizedVec.mul(speedPerBlocks);
                }
            }).get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void start(InstanceContainer container) {
        BallState.stayingStill = true;
        BallState.ballPosition = new Pos(0.5, 45.0, 0.5);
        hasPlayer = false;
        if (BallState.tasks != null && !BallState.tasks.isEmpty()) {
            BallState.tasks.forEach(tasks -> {
                tasks.forEach(Task::cancel);
                tasks.clear();
            });
            BallState.tasks.clear();
        }

        BallState.tasks = ParticleGenerator.spawnSphereParticles(container, BallState.ballPosition, 0.5, 0.5, 0.5, Particle.WAX_OFF, Double.POSITIVE_INFINITY);

        if (entity != null) {
            entity.remove(); // Remove the old ball entity
        }

        entity = new BallEntity(BallState.ballPosition, container); // Create the new ball entity

        BallState.isActive = true;
        BallState.firstTarget = true;
    }

    public boolean isPlayerAttached() {
        return this.hasPlayer;
    }

    public void setPlayerAttached(boolean hasPlayer) {
        this.hasPlayer = hasPlayer;
    }

    private void handleCollision(InstanceContainer container, Player target) {
        target.setGameMode(GameMode.SPECTATOR); // Send player to spectator mode
        ParticleGenerator.spawnCircleParticles(
                container, target.getPosition(), 0.5, 0.5, Particle.ITEM_SNOWBALL, 1
        ); // Generate visual effect

        synchronized (homedPlayerLock) {
            homedUponPlayer = null; // Clear the player target
            hasPlayer = false; // Allow targeting another player

        }

        // Cancel all related tasks
        synchronized (tasksLock) {
            BallState.tasks.forEach(taskGroup -> {
                taskGroup.forEach(Task::cancel);
                taskGroup.clear();
            });
            BallState.tasks.clear();
        }

        entity.remove(); // Remove ball entity
        start(container); // Restart game state
    }

    private void handleHomingLogic(InstanceContainer container) {
        if (homedUponPlayer != null) {
            // Calculate the movement vector to the homed target
            Vec movementVec = getMovementVec(homedUponPlayer);

            boolean shouldBounce;
            Vec bounceVec = movementVec;

            if (container.getBlock(BallState.ballPosition) != Block.AIR && hitWall < 60) {
                synchronized (ballPositionLock) {
                    BallState.ballPosition = BallState.ballPosition.sub(movementVec.mul(0.2)); // Adjust ball backward
                    bounceVec = movementVec.mul(-0.8); // Calculate bounce-back vector
                }
                hitWall++; // Increment wall hit counter
                shouldBounce = true;
            } else {
                shouldBounce = false;
            }

            // Synchronize on ball position lock
            /*synchronized (ballPositionLock) {
                if (container.getBlock(BallState.ballPosition) != Block.AIR && hitWall < 60) {
                    BallState.ballPosition = BallState.ballPosition.sub(movementVec.mul(0.2)); // Adjust ball backward
                    bounceVec = movementVec.mul(-0.8); // Calculate bounce-back vector
                    hitWall++; // Increment wall hit counter
                    shouldBounce = true;
                } else {
                    shouldBounce = false;
                }
            }*/

            // Perform homing movement

            if (shouldBounce) {
                doHoming(bounceVec, container);
            } else {
                doHoming(movementVec, container);
            }

            // Check if close enough to the target (collision)
            if (BallState.ballPosition.distanceSquared(homedUponPlayer.getPosition()) < 0.25) {
                handleCollision(container, homedUponPlayer);
            }
        }
    }

}
