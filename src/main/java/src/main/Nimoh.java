package src.main;

import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.extras.MojangAuth;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.LightingChunk;
import net.minestom.server.instance.anvil.AnvilLoader;
import net.minestom.server.instance.block.Block;
import net.minestom.server.timer.Scheduler;
import net.minestom.server.timer.TaskSchedule;
import src.main.core.ball.BladeBall;
import src.main.permission.PermissionHandler;
import src.main.permission.PermissionablePlayer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Scanner;


public non-sealed class Nimoh implements Server {

    public static GlobalEventHandler globalEventHandler;
    public static InstanceContainer instanceContainer;
    public static Scheduler scheduler;

    public static void main(String[] args) {
        MinecraftServer server = MinecraftServer.init();
        InstanceManager instanceManager = MinecraftServer.getInstanceManager();
        AnvilLoader anvilLoader = new AnvilLoader("worlds/world");
        Scanner scanner = new Scanner(System.in);
        instanceContainer = instanceManager.createInstanceContainer(anvilLoader);
        globalEventHandler = MinecraftServer.getGlobalEventHandler();
        scheduler = MinecraftServer.getSchedulerManager();

        MinecraftServer.getConnectionManager().setPlayerProvider(PermissionablePlayer::new);

        instanceContainer.setGenerator(unit -> {
            unit.modifier().setBlock(new Pos(0, 42, 0), Block.STONE);
        });

        instanceContainer.setChunkLoader(anvilLoader);
        Server.worldManager(instanceManager, List.of("worlds/world"));

        instanceContainer.setChunkSupplier(LightingChunk::new);

        Server.implementListeners();
        Server.registerCommands();

        MojangAuth.init();

        PermissionHandler.startHandler();

        server.start("0.0.0.0", scanner.nextInt());
        scheduleUpdate();

        new Thread(() -> {
            while (true) {
                if (scanner.next().equalsIgnoreCase("stop")) {
                    MinecraftServer.stopCleanly();
                    break;
                }
            }
        }).start();
    }

    private static void scheduleUpdate() {
        try {
            Method method = BladeBall.class.getDeclaredMethod("update", InstanceContainer.class);
            scheduler.scheduleTask(() -> {
                try {
                    method.invoke(new BladeBall(), instanceContainer);
                } catch (IllegalAccessException e) {
                    System.out.println("Something went wrong");
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    System.out.println("Something went wrong");
                    e.printStackTrace();
                }
                return TaskSchedule.tick(1);
            }, TaskSchedule.tick(1));
        } catch (NoSuchMethodException e) {
            System.out.println("Could not find method");
        }
    }
}
