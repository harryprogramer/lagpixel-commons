package lgpxl.servercommons.events.action.preaction;

import lgpxl.servercommons.ContextProvider;
import lgpxl.servercommons.events.config.Event;
import lgpxl.servercommons.events.config.EventLifecycle;
import lgpxl.servercommons.events.processor.EventProcessorManager;
import lgpxl.servercommons.events.scheduler.LagTask;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
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
    private HashMap<String, Location> previousLocation = new HashMap<>();
    private HashMap<String, GameMode> previousGamemode = new HashMap<>();
    private ContextProvider provider;

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
        previousLocation.put(event.getPlayer().getName(), event.getPlayer().getLocation());
        previousGamemode.put(event.getPlayer().getName(), event.getPlayer().getGameMode());
        event.getPlayer().teleport(new Location(Bukkit.getWorld("world"), 0.5, 256, 0.5));
        for(Player playerToShow : provider.getPlugin().getServer().getOnlinePlayers()) {
            event.getPlayer().hidePlayer(provider.getPlugin(), playerToShow);
        }
        event.getPlayer().setGameMode(GameMode.ADVENTURE);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerMove(@NotNull PlayerMoveEvent event) {
        Location location = event.getFrom();
        location.setX(0.5);
        location.setY(256);
        location.setX(0.5);
        event.setTo(location);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerQuit(@NotNull PlayerQuitEvent event) {
        try {
            restorePlayerLastState(event.getPlayer(), provider);
        }catch (Exception e){
            provider.getLogger().warning("Cannot restore last player state after event: " + e.getMessage());
        }
    }

    @Override
    public synchronized void onAction(ContextProvider server, EventProcessorManager processorManager, Event event, EventLifecycle lifecycle, Map<String, String> options) {
        if(!server.getPlugin().isEnabled()){
            return;
        }

        server.getPlugin().getServer().getScheduler().runTask(server.getPlugin(), () -> Objects.requireNonNull(Bukkit.getWorld("world")).setTime(1000));

        this.provider = server;

        server.getPlugin().getServer().getPluginManager().registerEvents(this, server.getPlugin());

        Logger logger = server.getLogger();


        previousLocation = new HashMap<>();
        previousGamemode = new HashMap<>();
        processorManager.setKickRule(false);
        try {
            buildBlackBox(server.getPlugin());
        }catch (Exception e){
            logger.warning("Cannot build black box for counting action");
            e.printStackTrace();
        }
        if(!server.getPlugin().getServer().getOnlinePlayers().isEmpty()) {
            for (Player player : server.getPlugin().getServer().getOnlinePlayers()) {
                previousLocation.put(player.getName(), player.getLocation());
                previousGamemode.put(player.getName(), player.getGameMode());
                for(Player playerToShow : server.getPlugin().getServer().getOnlinePlayers()) {
                    player.hidePlayer(server.getPlugin(), playerToShow);
                }
                server.getPlugin().getServer().getScheduler().runTask(server.getPlugin(), () -> player.setGameMode(GameMode.ADVENTURE));
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
                        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
                        player.showTitle(title);
                    }
                }
            }
            ), finalI * 1000L, TimeUnit.MILLISECONDS);
        }

        server.getScheduler().schedule(new LagTask(() -> {
            for(Player player : server.getPlugin().getServer().getOnlinePlayers()){
                if(!server.getPlugin().getServer().getOnlinePlayers().isEmpty()) {
                    final Component mainTitle = Component.text(options.get("finishText")).color(TextColor.color(0xFFD54B));
                    final Title.Times times = Title.Times.of(Duration.ofMillis(500), Duration.ofMillis(lifecycle.getPeriodTime()), Duration.ofMillis(2000));
                    final Title title = Title.title(mainTitle, Component.empty(), times);

                    server.getPlugin().getServer().getScheduler().runTask(server.getPlugin(), () -> player.showTitle(title));
                }
            }
        }), ChronoUnit.MILLIS.between(LocalDateTime.now(), event.getEventTime()),
                TimeUnit.MILLISECONDS);

        LagTask task = new LagTask(() -> {
            logger.info("Starting escape for PreAction [BlackScreenCounting].");

            for(Player player : server.getPlugin().getServer().getOnlinePlayers()){
                try {
                    restorePlayerLastState(player, server);
                }catch (Exception e){
                     logger.warning("Cannot restore last player state after event: " + e.getMessage());
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

    private void restorePlayerLastState(Player player, ContextProvider server){
        Logger logger = server.getLogger();

        Location location = previousLocation.get(player.getName());
        GameMode gameMode = previousGamemode.get(player.getName());
        for(Player playerToShow : server.getPlugin().getServer().getOnlinePlayers()) {
            player.showPlayer(server.getPlugin(), playerToShow);
        }

        if(gameMode != null) {
            server.getPlugin().getServer().getScheduler().runTask(server.getPlugin(), () -> player.setGameMode(gameMode));
        }else{
            logger.warning("Unknown previous GameMode for player [" + player.getName() + "], setting default.");
        }

        if(location != null) {
            try {
                logger.info("Teleporting player [" + player.getName() + "] to location: " + location);
                server.getPlugin().getServer().getScheduler().runTask(server.getPlugin(), () -> player.teleport(location));
            } catch (Throwable e) {
                logger.warning("Cannot teleport player to previous positions due: " + e.getMessage());
                e.printStackTrace();
            }
        }else {
            logger.warning("Cannot find previous location for [" + player.getName() + "], teleporting to 0, 0, 0");
            Block y = Objects.requireNonNull(Bukkit.getWorld("world")).getHighestBlockAt(0, 0);
            Location newLocation = new Location(Bukkit.getWorld("world"), 0, y.getY(), 0);
            logger.info("Teleporting player [" + player.getName() + "] to location: " + newLocation);
            server.getPlugin().getServer().getScheduler().runTask(server.getPlugin(), () -> player.teleport(newLocation));
        }
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
