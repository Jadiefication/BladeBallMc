package io.jadiefication;

import io.jadiefication.core.data.player.PlayerDataHandler;
import io.jadiefication.customitem.CustomItem;
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
import net.minestom.server.timer.Scheduler;
import net.minestom.server.timer.Task;
import net.minestom.server.timer.TaskSchedule;
import io.jadiefication.core.ball.BladeBall;
import io.jadiefication.permission.PermissionHandler;
import io.jadiefication.permission.PermissionablePlayer;

import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;


public abstract non-sealed class Nimoh implements Server, PlayerDataHandler {

    public static GlobalEventHandler globalEventHandler;
    public static InstanceContainer instanceContainer;
    public static Scheduler scheduler;
    public static Task updateTask;
    public static BladeBall game;
    public static InstanceManager instanceManager;
    public static final ExecutorService executorService = Executors.newCachedThreadPool();

    public static final ItemStack dash = CustomItem.registerItem(Component.text("§8Dash ability"), List.of(Component.text("Use this ability to dash forward")),
            Material.BLACK_STAINED_GLASS_PANE, 1, event -> {
                PlayerUseItemEvent e = (PlayerUseItemEvent) event;
        final Player player = e.getPlayer();
        AtomicReference<Pos> pos = new AtomicReference<>(player.getPosition());
        Vec direction = pos.get().direction();

        Vec movement = direction.mul(3);
        AtomicReference<Double> steps = new AtomicReference<>((double) 20);
        Vec step = movement.div(steps.get());
        AtomicReference<Task> taskReference = new AtomicReference<>();
        Task task = scheduler.scheduleTask(() -> {
            Pos newPos = pos.get().add(step);
            pos.set(newPos);
            player.teleport(new Pos(newPos.x(), pos.get().y(), newPos.z()));

            if (steps.get() <= 1) {
                // Cancel the task
                Task runningTask = taskReference.get();
                if (runningTask != null) {
                    runningTask.cancel(); // Stop the task
                }
                return;
            }


            steps.getAndSet(steps.get() - 1);
        }, TaskSchedule.tick(1), TaskSchedule.tick(1));
        taskReference.set(task);
    });

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
