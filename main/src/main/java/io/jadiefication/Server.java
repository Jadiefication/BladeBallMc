package io.jadiefication;

import io.jadiefication.commands.*;
import io.jadiefication.commands.collision.CollisionCommand;
import io.jadiefication.commands.collision.visibility.HideCommand;
import io.jadiefication.commands.collision.visibility.ShowCommand;
import io.jadiefication.commands.debug.DebugCommand;
import io.jadiefication.commands.permission.PermissionCommand;
import io.jadiefication.commands.timecommand.TimeCommand;
import io.jadiefication.commands.weathercommand.WeatherCommand;
import io.jadiefication.customitem.CustomItem;
import io.jadiefication.eventfunctions.EventFunction;
import io.jadiefication.permission.PermissionHandler;
import io.jadiefication.permission.sql.PermissionSQLHandler;
import io.jadiefication.util.game.prestart.collision.CollisionItem;
import net.kyori.adventure.resource.ResourcePackInfo;
import net.kyori.adventure.resource.ResourcePackRequest;
import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandManager;
import net.minestom.server.command.builder.Command;
import net.minestom.server.entity.Player;
import net.minestom.server.event.Event;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.inventory.InventoryCloseEvent;
import net.minestom.server.event.inventory.InventoryOpenEvent;
import net.minestom.server.event.inventory.InventoryPreClickEvent;
import net.minestom.server.event.item.PlayerBeginItemUseEvent;
import net.minestom.server.event.player.*;
import net.minestom.server.event.server.ServerListPingEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import java.net.URI;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public sealed interface Server permits Nimoh {

    ItemStack coin = CustomItem.registerItem(Component.text("Coin"), List.of(), Material.AMETHYST_SHARD, 2, ignored -> {
    });

    static void sendPackInfo(Player player) {
        player.sendResourcePacks(ResourcePackRequest.resourcePackRequest()
                .packs(getPackInfo())
                .prompt(Component.text("Please use this texture pack for a seamless experience."))
                .replace(true)
                .required(true)
                .build());
    }

    private static ResourcePackInfo getPackInfo() {
        return ResourcePackInfo.resourcePackInfo()
                .uri(URI.create("https://download.mc-packs.net/pack/948d23a556215fd92e141dfe6541524cdf3e3ec3.zip"))
                .hash("948d23a556215fd92e141dfe6541524cdf3e3ec3")
                .build();
    }

    static void shutdownBuilder(InstanceManager manager) {
        MinecraftServer.getSchedulerManager().buildShutdownTask(() -> {
            System.out.println("Starting server shutdown sequence...");
            
            // Save permissions first
            try {
                PermissionHandler.groupPermissions.forEach((group, p) -> 
                    PermissionSQLHandler.setPermissions(group));
            } catch (Exception e) {
                System.err.println("Failed to save permissions: " + e.getMessage());
            }

            // Notify players
            manager.getInstances().forEach(instance -> 
                instance.sendMessage(Component.text("§4§lServer shutting down")));

            // Save world data
            System.out.println("Saving world data...");
            CountDownLatch saveLatch = new CountDownLatch(manager.getInstances().size());
            
            manager.getInstances().forEach(instance -> {
                try {
                    System.out.println("Saving instance: " + instance.getUniqueId());
                    instance.saveChunksToStorage().thenRun(() -> {
                        System.out.println("Successfully saved instance: " + instance.getUniqueId());
                        saveLatch.countDown();
                    }).exceptionally(throwable -> {
                        System.err.println("Failed to save instance " + instance.getUniqueId() + 
                            ": " + throwable.getMessage());
                        saveLatch.countDown();
                        return null;
                    });
                } catch (Exception e) {
                    System.err.println("Error during save of instance " + instance.getUniqueId() + 
                        ": " + e.getMessage());
                    saveLatch.countDown();
                }
            });

            // Wait for all saves to complete (with timeout)
            try {
                if (!saveLatch.await(30, TimeUnit.SECONDS)) {
                    System.err.println("World save timed out after 30 seconds!");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("World save was interrupted!");
            }

            // Close database connection last
            try {
                if (Nimoh.connection != null && !Nimoh.connection.isClosed()) {
                    Nimoh.connection.close();
                    System.out.println("Database connection closed successfully");
                }
            } catch (SQLException e) {
                System.err.println("Failed to close database connection: " + e.getMessage());
            }

            System.out.println("Server shutdown sequence completed");
        });
    }


    static void registerCommands() {
        CommandManager manager = MinecraftServer.getCommandManager();
        List<Command> commands = List.of(new OpCommand(), new GamemodeCommand(), new StopCommand(), new TimeCommand(), new WeatherCommand(), new FillCommand(),
                new ParticleCommand(), new DebugCommand(), new StartCommand(), new StopBallCommand(), new PermissionCommand(), new ShowCommand(), new HideCommand(),
                new CollisionCommand());

        for (Command command : commands) {
            manager.register(command);
        }
    }

    static void implementListeners(GlobalEventHandler handler) {
        Map<Class<? extends Event>, Consumer<? extends Event>> events = Map.ofEntries(
                Map.entry(AsyncPlayerConfigurationEvent.class, (Consumer<AsyncPlayerConfigurationEvent>) EventFunction::onJoin),
                Map.entry(PlayerBlockBreakEvent.class, (Consumer<PlayerBlockBreakEvent>) EventFunction::onBreak),
                Map.entry(PlayerBlockPlaceEvent.class, (Consumer<PlayerBlockPlaceEvent>) EventFunction::onPlace),
                Map.entry(ServerListPingEvent.class, (Consumer<ServerListPingEvent>) EventFunction::onPing),
                Map.entry(InventoryPreClickEvent.class, (Consumer<InventoryPreClickEvent>) EventFunction::onInventoryClick),
                Map.entry(PlayerUseItemEvent.class, (Consumer<PlayerUseItemEvent>) EventFunction::onItemUse),
                Map.entry(PlayerDisconnectEvent.class, (Consumer<PlayerDisconnectEvent>) EventFunction::onLeave),
                Map.entry(InventoryOpenEvent.class, (Consumer<InventoryOpenEvent>) EventFunction::onInventoryOpen),
                Map.entry(PlayerSpawnEvent.class, (Consumer<PlayerSpawnEvent>) EventFunction::onWorldJoin),
                Map.entry(InventoryCloseEvent.class, (Consumer<InventoryCloseEvent>) EventFunction::onInventoryClose),
                Map.entry(PlayerBeginItemUseEvent.class, (Consumer<PlayerBeginItemUseEvent>) EventFunction::onItemBlock),
                Map.entry(PlayerStartDiggingEvent.class, (Consumer<PlayerStartDiggingEvent>) CollisionItem::onLeftClick),
                Map.entry(PlayerBlockInteractEvent.class, (Consumer<PlayerBlockInteractEvent>) CollisionItem::onRightClick)
        );

        for (Map.Entry<Class<? extends Event>, Consumer<? extends Event>> entry : events.entrySet()) {
            handler.addListener(entry.getKey(), (Consumer) entry.getValue());
        }
    }
}
