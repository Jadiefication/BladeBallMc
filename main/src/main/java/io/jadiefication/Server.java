package io.jadiefication;

import io.jadiefication.commands.*;
import io.jadiefication.commands.debug.DebugCommand;
import io.jadiefication.commands.permission.PermissionCommand;
import io.jadiefication.commands.timecommand.TimeCommand;
import io.jadiefication.commands.weathercommand.WeatherCommand;
import io.jadiefication.customitem.CustomItem;
import io.jadiefication.eventfunctions.EventFunction;
import io.jadiefication.permission.PermissionHandler;
import io.jadiefication.permission.PermissionableGroup;
import io.jadiefication.permission.sql.PermissionSQLHandler;
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
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

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

    static void worldManager(InstanceManager manager) {

        MinecraftServer.getSchedulerManager().buildShutdownTask(() -> {
            PermissionHandler.groupPermissions.forEach((group, p) -> {
                PermissionSQLHandler.setPermissions(group);
            });
            manager.getInstances().forEach(instance -> {
                instance.sendMessage(Component.text("§4§lServer shutting down"));
            });
            manager.getInstances().forEach(Instance::saveChunksToStorage);
            System.out.println("Saving chunks...");
        });
    }


    static void registerCommands() {
        CommandManager manager = MinecraftServer.getCommandManager();
        List<Command> commands = List.of(new OpCommand(), new GamemodeCommand(), new StopCommand(), new TimeCommand(), new WeatherCommand(), new FillCommand(),
                new ParticleCommand(), new DebugCommand(), new StartCommand(), new StopBallCommand(), new PermissionCommand());

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
                Map.entry(PlayerBeginItemUseEvent.class, (Consumer<PlayerBeginItemUseEvent>) EventFunction::onItemBlock)
        );

        for (Map.Entry<Class<? extends Event>, Consumer<? extends Event>> entry : events.entrySet()) {
            handler.addListener(entry.getKey(), (Consumer) entry.getValue());
        }
    }
}
