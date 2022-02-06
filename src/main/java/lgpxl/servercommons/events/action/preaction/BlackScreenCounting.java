package lgpxl.servercommons.events.action.preaction;

import com.fasterxml.jackson.databind.util.Named;
import lgpxl.servercommons.ContextProvider;
import lgpxl.servercommons.events.config.Event;
import lgpxl.servercommons.events.config.EventLifecycle;
import lgpxl.servercommons.events.processor.EventProcessorManager;
import lgpxl.servercommons.events.scheduler.LagTask;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class BlackScreenCounting extends AbstractPreAction implements Listener {
    private HashMap<String, Location> previousLocations = new HashMap<>();

    private final static NamedTextColor[] textColors = new NamedTextColor[]{
            NamedTextColor.RED,
            NamedTextColor.YELLOW,
            NamedTextColor.GREEN,
            NamedTextColor.BLUE,
            NamedTextColor.AQUA,
            NamedTextColor.DARK_PURPLE,
            NamedTextColor.LIGHT_PURPLE,
            NamedTextColor.GOLD,
            NamedTextColor.WHITE
    };

    private static final Location[] locations = new Location[]{
            new Location(Bukkit.getWorld("world"), 0, 255, 0),
            new Location(Bukkit.getWorld("world"), 0, 256, 1),
            new Location(Bukkit.getWorld("world"), 1, 256, 0),
            new Location(Bukkit.getWorld("world"), 0, 256, -1),
            new Location(Bukkit.getWorld("world"), -1, 256, 0),
            new Location(Bukkit.getWorld("world"), 0, 257, 1),
            new Location(Bukkit.getWorld("world"), 1, 257, 0),
            new Location(Bukkit.getWorld("world"), 0, 257, -1),
            new Location(Bukkit.getWorld("world"), -1, 257, 0),
            new Location(Bukkit.getWorld("world"), 0, 258, 0)
    };

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerJoin(@NotNull PlayerJoinEvent event) {
        previousLocations.put(event.getPlayer().getName(), event.getPlayer().getLocation());
        event.getPlayer().teleport(new Location(Bukkit.getWorld("world"), 0.5, 256, 0.5));
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerMove(@NotNull PlayerMoveEvent event) {
        event.setTo(event.getFrom());
    }

    @Override
    public void onAction(ContextProvider server, EventProcessorManager processorManager, Event event, EventLifecycle lifecycle, Map<String, String> options) {
        server.getPlugin().getServer().getPluginManager().registerEvents(this, server.getPlugin());

        Logger logger = server.getLogger();

        logger.info("Starting PreAction [BlackScreenCounting]");

        previousLocations = new HashMap<>();
        processorManager.setKickRule(false);
        try {
            buildBlackBox(server.getPlugin());
        }catch (Exception e){
            logger.warning("Cannot build black box for counting action");
            e.printStackTrace();
        }
        if(!server.getPlugin().getServer().getOnlinePlayers().isEmpty()) {
            for (Player player : server.getPlugin().getServer().getOnlinePlayers()) {
                previousLocations.put(player.getName(), player.getLocation());
                server.getPlugin().getServer().getScheduler().runTask(server.getPlugin(), () -> player.teleport(new Location(Bukkit.getWorld("world"), 0.5, 256, 0.5)));
            }
        }else {
            logger.info("No players currently on server, they will be handled by PlayerJoinEvent.");
        }

        int seconds = (int) (lifecycle.getTimeAfterStart() / 1000);

        for(int i = 0; i < seconds + 1; i++){
            int finalI = i;
            server.getScheduler().schedule(new LagTask(() -> {
                if(!server.getPlugin().getServer().getOnlinePlayers().isEmpty()) {
                    final Component mainTitle = Component.text(String.valueOf(seconds - finalI), textColors[((seconds - finalI) % textColors.length)]);
                    final Title title = Title.title(mainTitle, mainTitle);
                    for (Player player : server.getPlugin().getServer().getOnlinePlayers()) {
                        player.showTitle(title);
                    }
                }
            }
            ), finalI * 1000L, TimeUnit.MILLISECONDS);
        }

        server.getScheduler().schedule(new LagTask(() -> {
            for(Player player : server.getPlugin().getServer().getOnlinePlayers()){
                System.out.println("tak takt ktaks");
                final Component mainTitle = Component.text(options.get("finishText")).color(TextColor.color(0xFFD54B));
                final Title.Times times = Title.Times.of(Duration.ofMillis(500), Duration.ofMillis(lifecycle.getPeriodTime()), Duration.ofMillis(2000));
                final Title title = Title.title(mainTitle, Component.empty(), times);

                player.showTitle(title);
            }
        }), ChronoUnit.MILLIS.between(LocalDateTime.now(), event.getEventTime()),
                TimeUnit.MILLISECONDS);

        LagTask task = new LagTask(() -> {
            logger.info("Starting escape for PreAction [BlackScreenCounting].");
            for(Player player : server.getPlugin().getServer().getOnlinePlayers()){
                try {
                    Location previousLocation = previousLocations.get(player.getName());
                    if (previousLocation != null) {
                        logger.info("Teleporting player [" + player.getName() + "] to location: " + previousLocation);
                        server.getPlugin().getServer().getScheduler().runTask(server.getPlugin(), () -> player.teleport(previousLocation));
                    } else {
                        logger.warning("Cannot find previous location for [" + player.getName() + "], teleporting to 0, 0, 0");
                        Block y = Objects.requireNonNull(Bukkit.getWorld("world")).getHighestBlockAt(0, 0);
                        Location newLocation = new Location(Bukkit.getWorld("world"), 0, y.getY(), 0);
                        logger.info("Teleporting player [" + player.getName() + "] to location: " + newLocation);
                        server.getPlugin().getServer().getScheduler().runTask(server.getPlugin(), () -> player.teleport(newLocation));
                    }
                }catch (Throwable e){
                    logger.warning("Cannot teleport player to previous positions due: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            try {
                removeBlackBox(server.getPlugin());
                logger.info("Black box from PreAction [BlackScreenCounting] was destroyed.");
            }catch (Exception e){
                logger.warning("Cannot remove black box");
                e.printStackTrace();
            }
            HandlerList.unregisterAll(this);
        });

        logger.info("Scheduling PreAction [BlackScreenCounting] escape with task id: " + task.getTaskID());
        server.getScheduler().schedule(task, ChronoUnit.MILLIS.between(LocalDateTime.now(), event.getEventTime()), TimeUnit.MILLISECONDS);
    }

    private static void buildBlackBox(JavaPlugin plugin){
        for (Location location : locations) {
           BukkitTask task = plugin.getServer().getScheduler().runTask(plugin, () -> location.getBlock().setType(Material.BLACK_CONCRETE));
           while (Bukkit.getScheduler().isCurrentlyRunning(task.getTaskId()))
               Thread.onSpinWait();
        }
    }

    private static void removeBlackBox(JavaPlugin plugin){
        for (Location location : locations) {
             plugin.getServer().getScheduler().runTask(plugin, () -> location.getBlock().setType(Material.AIR));
        }
    }
}
