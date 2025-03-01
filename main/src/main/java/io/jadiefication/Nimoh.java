package io.jadiefication;

import io.jadiefication.util.game.start.ball.BladeBall;
import io.jadiefication.util.data.player.PlayerDataHandler;
import io.jadiefication.permission.PermissionHandler;
import io.jadiefication.permission.PermissionablePlayer;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.extras.MojangAuth;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.LightingChunk;
import net.minestom.server.instance.anvil.AnvilLoader;
import net.minestom.server.instance.block.Block;
import net.minestom.server.network.packet.server.play.SetExperiencePacket;
import net.minestom.server.timer.Scheduler;
import net.minestom.server.timer.Task;
import net.minestom.server.timer.TaskSchedule;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public abstract non-sealed class Nimoh implements Server, PlayerDataHandler, AbilitiesHolder {

    public static GlobalEventHandler globalEventHandler;
    public static InstanceContainer instanceContainer;
    public static Scheduler scheduler;
    public static Task updateTask;
    public static BladeBall game;
    public static InstanceManager instanceManager;
    public static final ExecutorService executorService = Executors.newCachedThreadPool();
    public static String url = "jdbc:sqlite:data/db/data.db";
    public static boolean testing = true;
    public static File confFile = new File("server.properties");
    public static String config;
    public static Connection connection;

    static {
        try {
            config = Files.readString(confFile.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws IOException {
        MinecraftServer server = MinecraftServer.init();
        instanceManager = MinecraftServer.getInstanceManager();
        if (new File("worlds").exists()) {
            Files.move(new File("worlds").toPath(), new File("data/worlds").toPath());
        }
        AnvilLoader anvilLoader = new AnvilLoader("data/worlds/world");
        instanceContainer = instanceManager.createInstanceContainer(anvilLoader);
        globalEventHandler = MinecraftServer.getGlobalEventHandler();
        scheduler = MinecraftServer.getSchedulerManager();

        File dbDir = new File("data/db");
        dbDir.mkdirs(); // Create parent directories if they don't exist

        File file = new File("data/db/data.db");
        if (!file.exists()) {
            try {
                file.createNewFile();
                boolean ignored = file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        MinecraftServer.getConnectionManager().setPlayerProvider(PermissionablePlayer::new);

        loadWorld(anvilLoader);

        Server.implementListeners(globalEventHandler);
        Server.registerCommands();

        MojangAuth.init();

        try {
            PlayerDataHandler.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        PermissionHandler.startHandler();

        int port = Integer.parseInt(config.split("port=")[1].split("\n")[0]);

        server.start("0.0.0.0", port);
        game = new BladeBall();

        scheduler.scheduleTask(() -> {
            Iterator<Map.Entry<UUID, Long>> iterator = AbilitiesHolder.cooldownMap.entrySet().iterator();

            while (iterator.hasNext()) {
                Map.Entry<UUID, Long> entry = iterator.next();
                UUID uuid = entry.getKey();
                long newTime = entry.getValue() - 1;
                float percentage = (float) newTime / cooldown; // Assuming max cooldown is 30 seconds
                Player player = MinecraftServer.getConnectionManager().getOnlinePlayerByUuid(uuid);

                if (player != null) {
                    if (newTime <= 0) {
                        iterator.remove(); // Safe removal from the map
                        player.sendPacket(new SetExperiencePacket(0, 0, 0));
                    } else {
                        entry.setValue(newTime); // Update the current value
                    }

                    if (AbilitiesHolder.cooldownMap.containsKey(player.getUuid())) {
                        player.sendPacket(new SetExperiencePacket(percentage, (int) newTime, 0));
                    }
                }
            }
        }, TaskSchedule.seconds(1), TaskSchedule.seconds(1));

        scheduler.scheduleTask(() -> {
            Iterator<Map.Entry<Player, Integer>> iterator = BladeBall.shieldCooldown.entrySet().iterator();

            while (iterator.hasNext()) {
                Map.Entry<Player, Integer> entry = iterator.next();
                Player player = entry.getKey();
                int newTime = entry.getValue() - 1;
                float percentage = (float) newTime / 30; // Assuming max cooldown is 30 seconds

                if (player != null) {
                    if (newTime <= 0) {
                        iterator.remove(); // Safe removal from the map
                        player.sendPacket(new SetExperiencePacket(0, 0, 0));
                    } else {
                        entry.setValue(newTime); // Update the current value
                    }

                    if (BladeBall.shieldCooldown.containsKey(player)) {
                        player.sendPacket(new SetExperiencePacket(percentage, newTime, 0));
                    }
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
        Server.shutdownBuilder(instanceManager);

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
