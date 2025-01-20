package io.jadiefication.core.ball;

import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.block.Block;
import net.minestom.server.particle.Particle;
import net.minestom.server.potion.Potion;
import net.minestom.server.potion.PotionEffect;
import net.minestom.server.scoreboard.Scoreboard;
import net.minestom.server.scoreboard.Team;
import net.minestom.server.scoreboard.TeamBuilder;
import net.minestom.server.scoreboard.TeamManager;
import net.minestom.server.timer.Task;
import io.jadiefication.core.ball.entity.BallEntity;
import io.jadiefication.particlegenerator.ParticleGenerator;

import java.util.*;

public non-sealed class BladeBall implements BallHandler {

    public static BallEntity entity;
    private static Player homedUponPlayer;
    public static boolean hasPlayer = false;
    private int hitWall;
    private List<List<Task>> tasks;
    private static TeamManager manager = new TeamManager();
    private static Team target = new TeamBuilder("target", manager)
            .build();
    private static Team others = new TeamBuilder("others", manager)
            .build();

    @Override
    public void update(InstanceContainer container) {
        int dt = BallState.dt;

        BallState.dtCounter.cancel();
        BallState.dt = 0;

        if (!hasPlayer) {
            homedUponPlayer = BallState.firstTarget ? BallState.findFirstTarget(container) : findTarget(container);
            BallState.firstTarget = false;
            hasPlayer = true;
            if (homedUponPlayer != null) {
                target.addMember(homedUponPlayer.getUsername());
                List<Player> players = new java.util.ArrayList<>(container.getPlayers().stream().toList());
                players.remove(homedUponPlayer);
                players.forEach(player -> others.addMember(player.getUsername()));
            }
        }

        if (homedUponPlayer != null) {
            if (tasks != null) {
                tasks.forEach(taskList -> taskList.forEach(Task::cancel));
                tasks.clear();
            }
            tasks = ParticleGenerator.spawnCircleParticles(container, homedUponPlayer.getPosition(), 1, 1, Particle.FLAME, Double.POSITIVE_INFINITY);
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
                    ParticleGenerator.spawnSphereParticles(container, homedUponPlayer.getPosition(), 0.5, 0.5, 0.5, Particle.ITEM_SNOWBALL, 1);
                    homedUponPlayer = null;
                    BallState.tasks.forEach(tasks -> tasks.forEach(Task::cancel));
                    BallState.tasks.clear();
                    tasks.forEach(taskList -> taskList.forEach(Task::cancel));
                    tasks.clear();
                    container.getPlayers().forEach(player -> {
                        if (!homedUponPlayer.equals(player)) {
                            others.removeMember(player.getUsername());
                        }
                    });
                    target.removeMember(homedUponPlayer.getUsername());
                    entity.remove();
                    homedUponPlayer.clearEffects();
                    start(container);
                }
            } catch (NullPointerException ignored) {

            }
        } else hasPlayer = false;
    }

    private static void doHoming(Vec movementVec, InstanceContainer container) {
        entity.remove();
        BallState.ballPosition = BallState.ballPosition.add(movementVec);

        BallState.tasks.forEach(tasks -> tasks.forEach(Task::cancel));
        BallState.tasks.clear();
        List<Player> players = new java.util.ArrayList<>(container.getPlayers().stream().toList());
        players.remove(homedUponPlayer);
        if (!players.isEmpty()) {
            BallState.tasks = ParticleGenerator.spawnSphereParticles(
                    BallState.ballPosition, 0.5, 0.5, 0.5, Map.of(Particle.WAX_ON, target, Particle.WAX_OFF, others), 1.0
            );
        } else {
            BallState.tasks = ParticleGenerator.spawnSphereParticles(container, BallState.ballPosition, 0.5, 0.5, 0.5, Particle.WAX_ON, 1.0);
        }
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
        container.getPlayers().forEach(player -> player.addEffect(new Potion(PotionEffect.GLOWING, 1, Integer.MAX_VALUE)));
        BallState.stayingStill = true;
        BallState.ballPosition = new Pos(0.5, 45.0, 0.5);
        hasPlayer = false;

        BallState.tasks = ParticleGenerator.spawnSphereParticles(container, BallState.ballPosition, 0.5, 0.5, 0.5, Particle.WAX_OFF, Double.POSITIVE_INFINITY);
        entity = new BallEntity(BallState.ballPosition, container);

        BallState.isActive = true;
        BallState.firstTarget = true;
    }
}
