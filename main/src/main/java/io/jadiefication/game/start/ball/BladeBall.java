package io.jadiefication.game.start.ball;

import io.jadiefication.Nimoh;
import io.jadiefication.Server;
import io.jadiefication.game.start.ball.entity.BallEntity;
import io.jadiefication.game.start.Match;
import io.jadiefication.game.start.team.GameTeam;
import io.jadiefication.game.start.team.TeamHandler;
import io.jadiefication.game.prestart.vote.VoteGamemode;
import io.jadiefication.game.prestart.vote.VoteHandler;
import io.jadiefication.particlegenerator.ParticleGenerator;
import io.jadiefication.particlegenerator.packets.PacketReceiver;
import io.jadiefication.permission.PermissionablePlayer;
import net.jadiefication.stream.StreamExpander;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.advancements.FrameType;
import net.minestom.server.advancements.Notification;
import net.minestom.server.adventure.audience.PacketGroupingAudience;
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
import net.minestom.server.network.packet.server.play.ParticlePacket;
import net.minestom.server.network.packet.server.play.SetTitleTextPacket;
import net.minestom.server.network.packet.server.play.TeamsPacket;
import net.minestom.server.particle.Particle;
import net.minestom.server.scoreboard.Team;
import net.minestom.server.scoreboard.TeamBuilder;
import net.minestom.server.scoreboard.TeamManager;

import java.util.*;
import java.util.concurrent.CompletableFuture;


/**
 * Scheduler... FUCK YOU
 *
 * @Author Jade
 */
public non-sealed class BladeBall implements BallHandler, VoteHandler, TeamHandler {

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
    private static final Notification winnerNotification = new Notification(Component.text("You gained 20 Coins", NamedTextColor.GOLD), FrameType.CHALLENGE, Server.coin);
    public static final Map<Player, Integer> shieldCooldown = new HashMap<>();
    private static Match mainMatch;

    @Override
    public void update(InstanceContainer container) {
        mainMatch.time += 1;

        if (Vote.gamemode != null && Vote.gamemode.equals(VoteGamemode.TEAM)) {
            if (TeamHandler.getInstance().isEmpty().isPresent()) {
                GameTeam team = TeamHandler.getInstance().isEmpty().get();
                GameTeam winner = TeamHandler.getInstance().getOpposingTeam(team);
                PermissionablePlayer[] players = new PermissionablePlayer[winner.getPlayers().size()];
                ((StreamExpander<PermissionablePlayer>) winner.getPlayers().stream()).forEachIndexed((player, index) -> players[index] = player);
                sendWin(players);
            }
        } else {
            if (mainMatch.getPlayers().size() == 1 && !Nimoh.testing) {
                sendWin((PermissionablePlayer) container.getPlayers().toArray()[0]);
                Nimoh.updateTask.cancel();
                BallState.task.cancel();
                VoteHandler.getInstance().restart();
            }
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
        List<Player> players = new ArrayList<>(mainMatch.getPlayers());
        players.remove(homedUponPlayer);
        String homedUsername;

        synchronized (homedPlayerLock) {
            homedUsername = homedUponPlayer.getUsername();
        }

        players.forEach(player -> playerNames.add(player.getUsername()));
        synchronized (teamLock) {
            target.addMember(homedUsername);
            other.addMembers(playerNames);
        }

        synchronized (ballPositionLock) {
            BallState.ballPosition = BallState.ballPosition.add(movementVec);
        }
        entity.teleport(BallState.ballPosition);
        // Always create new particle tasks after movement
        Map<Particle, List<? extends PacketGroupingAudience>> particleMap = new HashMap<>();
        particleMap.put(Particle.WAX_ON, List.of(target));
        particleMap.put(Particle.WAX_OFF, List.of(other));

        synchronized (tasksLock) {
            // Clear existing tasks
            BallState.task.cancel();
            BallState.task = ParticleGenerator.spawnBladeBall(
                    BallState.ballPosition,
                    new PacketReceiver(particleMap),
                    1.0
            );
        }
    }

    private static void sendWin(PermissionablePlayer... players) {
        for (PermissionablePlayer player : players) {
            player.sendPackets(new SetTitleTextPacket(Component.text("Winner")), new ParticlePacket(Particle.TOTEM_OF_UNDYING,
                    player.getPosition(), Pos.ZERO, 0, 1));
            player.sendNotification(winnerNotification);
            player.currencyAmount += 20;
            player.winAmount++;
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
        mainMatch = new Match(container.getPlayers());
        mainMatch.getPlayers().forEach(player -> {
            if (homedUponPlayer != null && player != homedUponPlayer) {
                other.removeMember(player.getUsername());
            } else if (player == homedUponPlayer) target.removeMember(player.getUsername());
        });
        mainMatch.time = 0;

        BallState.stayingStill = true;
        BallState.ballPosition = new Pos(0.5, 50.0, 0.5);
        hasPlayer = false;
        if (BallState.task != null) {
            BallState.task.cancel();
        }

        BallState.task = ParticleGenerator.spawnSphereParticles(BallState.ballPosition, 0.5, 0.5, 0.5, new PacketReceiver(container, Particle.WAX_OFF), Double.POSITIVE_INFINITY);

        if (entity != null) {
            entity.remove(); // Remove the old ball entity
        }

        entity = new BallEntity(BallState.ballPosition, container); // Create the new ball entity

        BallState.isActive = true;
        BallState.firstTarget = true;

        if (Objects.equals(Vote.gamemode, VoteGamemode.TEAM)) {
            TeamHandler.getInstance().start(container);
        }
    }

    public boolean isPlayerAttached() {
        return this.hasPlayer;
    }

    public void setPlayerAttached(boolean hasPlayer) {
        this.hasPlayer = hasPlayer;
    }

    private void handleCollision(InstanceContainer container, Player target) {
        //ParticleGenerator.spawnCircleParticles()
        if (BallState.playerWhomHitTheBall != null) {
            BallState.playerWhomHitTheBall.sendPacket(new ParticlePacket(Particle.TOTEM_OF_UNDYING,
                    BallState.playerWhomHitTheBall.getPosition(), Pos.ZERO, 0, 1));
        }
        target.setGameMode(GameMode.SPECTATOR); // Send player to spectator mode
        mainMatch.removePlayer(target);
        ParticleGenerator.spawnCircleParticles(
                target.getPosition(), 0.5, 0.5, new PacketReceiver(container ,Particle.ITEM_SNOWBALL), 1
        ); // Generate visual effect

        synchronized (homedPlayerLock) {
            homedUponPlayer = null; // Clear the player target
            hasPlayer = false; // Allow targeting another player

        }

        if (Vote.gamemode.equals(VoteGamemode.TEAM)) {
            GameTeam.getTeam(target).removePlayer(target);
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

    public static boolean isInMatch(Player player) {
        return mainMatch.getPlayers().contains(((PermissionablePlayer) player));
    }

}
