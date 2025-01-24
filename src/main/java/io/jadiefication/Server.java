package io.jadiefication;

import net.kyori.adventure.resource.ResourcePackInfo;
import net.kyori.adventure.resource.ResourcePackRequest;
import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandManager;
import net.minestom.server.command.builder.Command;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.entity.EntityAttackEvent;
import net.minestom.server.event.inventory.InventoryOpenEvent;
import net.minestom.server.event.inventory.InventoryPreClickEvent;
import net.minestom.server.event.player.*;
import net.minestom.server.event.server.ServerListPingEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.InstanceManager;
import io.jadiefication.commands.*;
import io.jadiefication.commands.debug.DebugCommand;
import io.jadiefication.commands.particlecommand.ThreeDimensionalParticleCommand;
import io.jadiefication.commands.particlecommand.TwoDimensionalParticleCommand;
import io.jadiefication.commands.timecommand.TimeCommand;
import io.jadiefication.commands.weathercommand.WeatherCommand;
import io.jadiefication.eventfunctions.EventFunction;

import java.io.File;
import java.net.URI;
import java.util.List;

public sealed interface Server permits Nimoh {

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
                .uri(URI.create("https://download.mc-packs.net/pack/d663cb18e12c5aa9c894ebeead28521ea1aae020.zip"))
                .hash("d663cb18e12c5aa9c894ebeead28521ea1aae020")
                .build();
    }

    static void worldManager(InstanceManager manager, List<String> worldPaths) {

        MinecraftServer.getSchedulerManager().buildShutdownTask(() -> {
            manager.getInstances().forEach(instance -> {
                instance.sendMessage(Component.text("§4§lServer shutting down"));
            });
            for (String worldPath : worldPaths) {
                File worldFolder = new File(worldPath);
                boolean ignored = worldFolder.delete();
            }
            System.out.println("Saving chunks...");
            manager.getInstances().forEach(Instance::saveChunksToStorage);
        });
    }

    static void registerCommands() {
        CommandManager manager = MinecraftServer.getCommandManager();
        List<Command> commands = List.of(new OpCommand(), new GamemodeCommand(), new StopCommand(), new TwoDimensionalParticleCommand(), new TimeCommand(), new WeatherCommand(), new FillCommand(),
                new ThreeDimensionalParticleCommand(), new DebugCommand(), new StartCommand(), new StopBallCommand());

        for (Command command : commands) {
            manager.register(command);
        }
    }

    static void implementListeners(GlobalEventHandler handler) {
        handler.addListener(AsyncPlayerConfigurationEvent.class, EventFunction::onJoin);
        handler.addListener(PlayerBlockBreakEvent.class, EventFunction::onBreak);
        handler.addListener(PlayerUseItemEvent.class, EventFunction::onUse);
        handler.addListener(PlayerBlockPlaceEvent.class, EventFunction::onPlace);
        handler.addListener(ServerListPingEvent.class, EventFunction::onPing);
        handler.addListener(InventoryPreClickEvent.class, EventFunction::onInventoryClick);
        handler.addListener(PlayerDisconnectEvent.class, EventFunction::onLeave);
        handler.addListener(EntityAttackEvent.class, EventFunction::onBallHit);
        handler.addListener(InventoryOpenEvent.class, EventFunction::onInventoryOpen);

    }
}
