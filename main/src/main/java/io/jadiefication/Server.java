package io.jadiefication;

import io.jadiefication.commands.*;
import io.jadiefication.commands.debug.DebugCommand;
import io.jadiefication.commands.particlecommand.ThreeDimensionalParticleCommand;
import io.jadiefication.commands.particlecommand.TwoDimensionalParticleCommand;
import io.jadiefication.commands.timecommand.TimeCommand;
import io.jadiefication.commands.weathercommand.WeatherCommand;
import io.jadiefication.customitem.CustomItem;
import io.jadiefication.eventfunctions.EventFunction;
import net.kyori.adventure.resource.ResourcePackInfo;
import net.kyori.adventure.resource.ResourcePackRequest;
import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandManager;
import net.minestom.server.command.builder.Command;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.entity.EntityAttackEvent;
import net.minestom.server.event.inventory.InventoryCloseEvent;
import net.minestom.server.event.inventory.InventoryOpenEvent;
import net.minestom.server.event.inventory.InventoryPreClickEvent;
import net.minestom.server.event.player.*;
import net.minestom.server.event.server.ServerListPingEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import java.net.URI;
import java.util.List;

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
            manager.getInstances().forEach(instance -> {
                instance.sendMessage(Component.text("§4§lServer shutting down"));
            });
            manager.getInstances().forEach(Instance::saveChunksToStorage);
            System.out.println("Saving chunks...");
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
        handler.addListener(PlayerUseItemEvent.class, EventFunction::onItemUse);
        handler.addListener(PlayerSpawnEvent.class, EventFunction::onWorldJoin);
        handler.addListener(InventoryCloseEvent.class, EventFunction::onInventoryClose);

    }
}
