package io.jadiefication.core.ball;

import io.jadiefication.Nimoh;
import io.jadiefication.Server;
import io.jadiefication.core.ball.entity.BallEntity;
import io.jadiefication.particlegenerator.ParticleGenerator;
import io.jadiefication.permission.PermissionablePlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.advancements.FrameType;
import net.minestom.server.advancements.Notification;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemComponent;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.item.component.UseCooldown;
import net.minestom.server.network.packet.server.play.SetTitleTextPacket;
import net.minestom.server.network.packet.server.play.TeamsPacket;
import net.minestom.server.particle.Particle;
import net.minestom.server.potion.Potion;
import net.minestom.server.potion.PotionEffect;
import net.minestom.server.scoreboard.Team;
import net.minestom.server.scoreboard.TeamBuilder;
import net.minestom.server.scoreboard.TeamManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;


/**
 * Scheduler... FUCK YOU
 *
 * @Author Jade
 */
public non-sealed class BladeBall implements BallHandler {

    private static final double MAX_SPEED = 10.0;
    public static BallEntity entity;
    private static volatile Player homedUponPlayer;
    public boolean hasPlayer = false;
    private int hitWall;
    private static final Object ballPositionLock = new Object();
    private static final Object homedPlayerLock = new Object();
    private static final Object tasksLock = new Object();
    private static final Object teamLock = new Object();
    public static final TeamManager manager = new TeamManager();
    private static final Team target = new TeamBuilder("target", manager)
            .teamColor(NamedTextColor.RED)
            .build();
    private static final Team other = new TeamBuilder("other", manager)
            .teamColor(NamedTextColor.WHITE)
            .collisionRule(TeamsPacket.CollisionRule.ALWAYS)
            .build();
    private static double speedPerBlocks = 0.5;
    public static final ItemStack item = ItemStack.builder(Material.DIAMOND_SWORD)
            .set(ItemComponent.USE_COOLDOWN, new UseCooldown(5, "sword"))
            .build();
    private static final List<Player> targetList = new ArrayList<>();
    private static List<Player> otherList = new ArrayList<>();
    private static final Notification winnerNotification = new Notification(Component.text("You gained 20 Coins", NamedTextColor.GOLD), FrameType.CHALLENGE, Server.coin);

    @Override
    public void update(InstanceContainer container) {
        int dt = BallState.dt;

        BallState.dtCounter.cancel();
        BallState.dt = 0;

        if (container.getPlayers().size() == 1) {
            PermissionablePlayer player = (PermissionablePlayer) container.getPlayers().toArray()[0];
            player.sendPacket(new SetTitleTextPacket(Component.text("Winner")));
            player.sendNotification(winnerNotification);
            player.currencyAmount += 20;
            Nimoh.updateTask.cancel();
            BallState.task.cancel();
        }

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
        } else {
            handleHomingLogic(container);
        }
    }

    private static void doHoming(Vec movementVec, InstanceContainer container) {
        List<String> playerNames = new ArrayList<>();
        List<Player> players = new ArrayList<>(container.getPlayers().stream().toList());
        players.remove(homedUponPlayer);
        String homedUsername;

        synchronized (homedPlayerLock) {
            homedUsername = homedUponPlayer.getUsername();
        }

        players.forEach(player -> playerNames.add(player.getUsername()));
        synchronized (teamLock) {
            target.addMember(homedUsername);
            other.addMembers(playerNames);
            targetList.addFirst(homedUponPlayer);
            otherList = players;
        }

        synchronized (ballPositionLock) {
            BallState.ballPosition = BallState.ballPosition.add(movementVec);
        }
        entity.teleport(BallState.ballPosition);
        // Always create new particle tasks after movement

        synchronized (tasksLock) {
            // Clear existing tasks
            BallState.task.cancel();
            BallState.task = ParticleGenerator.spawnSphereParticles(
                    BallState.ballPosition,
                    0.5, 0.5, 0.5,
                    Map.of(Particle.WAX_ON, targetList, Particle.WAX_OFF, otherList),
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
        container.getPlayers().forEach(player -> {
            player.addEffect(new Potion(PotionEffect.GLOWING, 1, Integer.MAX_VALUE));
            if (homedUponPlayer != null && player != homedUponPlayer) {
                other.removeMember(player.getUsername());
            } else if (player == homedUponPlayer) target.removeMember(player.getUsername());
        });

        otherList.clear();
        targetList.clear();

        BallState.stayingStill = true;
        BallState.ballPosition = new Pos(0.5, 50.0, 0.5);
        hasPlayer = false;
        if (BallState.task != null) {
            BallState.task.cancel();
        }

        BallState.task = ParticleGenerator.spawnSphereParticles(container, BallState.ballPosition, 0.5, 0.5, 0.5, Particle.WAX_OFF, Double.POSITIVE_INFINITY);

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
            BallState.task.cancel();
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
        } else hasPlayer = false;

    }

    public void multipleSpeed(double multiplier) {
        speedPerBlocks = speedPerBlocks + speedPerBlocks * multiplier;
        if (speedPerBlocks > MAX_SPEED) {
            speedPerBlocks = MAX_SPEED;
        }

    }

    public static boolean isHomedUponPlayer(Player player) {
        return player.equals(homedUponPlayer);
    }

}
