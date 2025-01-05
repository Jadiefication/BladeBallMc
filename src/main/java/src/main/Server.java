package src.main;

import net.kyori.adventure.resource.ResourcePackInfo;
import net.kyori.adventure.resource.ResourcePackRequest;
import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandManager;
import net.minestom.server.command.builder.Command;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.event.player.PlayerBlockBreakEvent;
import net.minestom.server.event.player.PlayerBlockPlaceEvent;
import net.minestom.server.event.player.PlayerUseItemEvent;
import net.minestom.server.event.server.ServerListPingEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.InstanceManager;
import src.main.commands.FillCommand;
import src.main.commands.GamemodeCommand;
import src.main.commands.OpCommand;
import src.main.commands.StopCommand;
import src.main.commands.particlecommand.ThreeDimensionalParticleCommand;
import src.main.commands.particlecommand.TwoDimensionalParticleCommand;
import src.main.commands.timecommand.TimeCommand;
import src.main.commands.weathercommand.WeatherCommand;
import src.main.eventfunctions.EventFunction;

import java.io.File;
import java.net.URI;
import java.util.List;

public class Server {

    public static void sendPackInfo(Player player) {
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

    public static void worldManager(InstanceManager manager, List<String> worldPaths) {

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
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static net.minestom.server.network.socket.Server getServer() {
        return MinecraftServer.getServer();
    }

    public static void registerCommands() {
        CommandManager manager = MinecraftServer.getCommandManager();
        List<Command> commands = List.of(new OpCommand(), new GamemodeCommand(), new StopCommand(), new TwoDimensionalParticleCommand(), new TimeCommand(), new WeatherCommand(), new FillCommand(),
                new ThreeDimensionalParticleCommand());

        for (Command command : commands) {
            manager.register(command);
        }
    }

    public static void implementListeners() {
        Nimoh.globalEventHandler.addListener(AsyncPlayerConfigurationEvent.class, EventFunction::onJoin);
        Nimoh.globalEventHandler.addListener(PlayerBlockBreakEvent.class, EventFunction::onBreak);
        Nimoh.globalEventHandler.addListener(PlayerUseItemEvent.class, EventFunction::onUse);
        Nimoh.globalEventHandler.addListener(PlayerBlockPlaceEvent.class, EventFunction::onPlace);
        Nimoh.globalEventHandler.addListener(ServerListPingEvent.class, EventFunction::onPing);
    }
}
