package io.jadiefication;

import io.jadiefication.core.ball.BladeBall;
import io.jadiefication.core.data.player.PlayerDataHandler;
import io.jadiefication.customitem.CustomItem;
import io.jadiefication.particlegenerator.ParticleGenerator;
import io.jadiefication.permission.PermissionHandler;
import io.jadiefication.permission.PermissionablePlayer;
import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.PlayerUseItemEvent;
import net.minestom.server.extras.MojangAuth;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.LightingChunk;
import net.minestom.server.instance.anvil.AnvilLoader;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.network.packet.server.play.ParticlePacket;
import net.minestom.server.network.packet.server.play.SetExperiencePacket;
import net.minestom.server.network.packet.server.play.TeamsPacket;
import net.minestom.server.particle.Particle;
import net.minestom.server.scoreboard.Team;
import net.minestom.server.scoreboard.TeamBuilder;
import net.minestom.server.timer.Scheduler;
import net.minestom.server.timer.Task;
import net.minestom.server.timer.TaskSchedule;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;


public abstract non-sealed class Nimoh implements Server, PlayerDataHandler, CustomItemsHolder {

    public static GlobalEventHandler globalEventHandler;
    public static InstanceContainer instanceContainer;
    public static Scheduler scheduler;
    public static Task updateTask;
    public static BladeBall game;
    public static InstanceManager instanceManager;
    public static final ExecutorService executorService = Executors.newCachedThreadPool();

    public static Pos collisionDetection(Pos pos, Player player) {
        if (pos.y() < 43) {
            pos = new Pos(pos.x(), 44, pos.z());
        }
        if (pos.y() > 54) {
            return player.getPosition();
        }
        if (instanceContainer.getBlock(pos) != Block.AIR) {
            return collisionDetection(pos.add(0, 1, 0), player);
        }
        return pos;
    }

    public static void main(String[] args) {
        MinecraftServer server = MinecraftServer.init();
        instanceManager = MinecraftServer.getInstanceManager();
        AnvilLoader anvilLoader = new AnvilLoader("worlds/world");
        Scanner scanner = new Scanner(System.in);
        instanceContainer = instanceManager.createInstanceContainer(anvilLoader);
        globalEventHandler = MinecraftServer.getGlobalEventHandler();
        scheduler = MinecraftServer.getSchedulerManager();

        MinecraftServer.getConnectionManager().setPlayerProvider(PermissionablePlayer::new);

        loadWorld(anvilLoader);

        Server.implementListeners(globalEventHandler);
        Server.registerCommands();

        MojangAuth.init();

        PermissionHandler.startHandler();

        PlayerDataHandler.start();

        server.start("0.0.0.0", scanner.nextInt());
        game = new BladeBall();

        scheduler.scheduleTask(() -> {
            Iterator<Map.Entry<UUID, Long>> iterator = CustomItemsHolder.cooldownMap.entrySet().iterator();

            while (iterator.hasNext()) {
                Map.Entry<UUID, Long> entry = iterator.next();
                UUID uuid = entry.getKey();
                long newTime = entry.getValue() - 1;
                float percentage = (float) newTime / 30; // Assuming max cooldown is 30 seconds
                Player player = MinecraftServer.getConnectionManager().getOnlinePlayerByUuid(uuid);

                if (newTime <= 0) {
                    iterator.remove(); // Safe removal from the map
                    player.sendPacket(new SetExperiencePacket(0, 0, 0));
                } else {
                    entry.setValue(newTime); // Update the current value
                }

                if (player != null && CustomItemsHolder.cooldownMap.containsKey(player.getUuid())) {
                    player.sendPacket(new SetExperiencePacket(percentage, (int) newTime, 0));
                }
            }
        }, TaskSchedule.seconds(1), TaskSchedule.seconds(1));
    }

    private static void loadWorld(AnvilLoader anvilLoader) {
        instanceContainer.setGenerator(unit -> {
            try {
                unit.modifier().setBlock(new Pos(0, 42, 0), Block.STONE);
            } catch (Exception ignored) {

            }
        });

        instanceContainer.setChunkLoader(anvilLoader);
        Server.worldManager(instanceManager, List.of("worlds/world"));

        instanceContainer.setChunkSupplier(LightingChunk::new);
    }

    public static void startBladeBall() {
        if (updateTask != null) {
            updateTask.cancel(); // Cancel previous task if active
        }

        game.start(instanceContainer);

        updateTask = scheduler.scheduleTask(() -> {
            game.update(instanceContainer);
            return TaskSchedule.tick(1);
        }, TaskSchedule.tick(1));
    }

}
