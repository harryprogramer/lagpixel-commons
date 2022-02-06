package lgpxl.servercommons.events.animation;

import lgpxl.servercommons.ContextProvider;
import lgpxl.servercommons.Lagpixel;
import lgpxl.servercommons.events.config.EventLifecycle;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;

public class FireworksAnimation implements Animation, Listener {
    private static final Color[] colors = new Color[]{Color.LIME, Color.BLUE, Color.PURPLE, Color.YELLOW, Color.RED, Color.AQUA, Color.ORANGE, Color.MAROON};

    @Override
    public void runAnimation(ContextProvider server, EventLifecycle lifecycle, Map<String, String> options) {
        long startWaitingTime = System.currentTimeMillis();
        while (!server.getPlugin().getServer().getPluginManager().isPluginEnabled(Lagpixel.NAME)
                && !((System.currentTimeMillis() - startWaitingTime) / 1000.0 < 5000))
            Thread.onSpinWait();

        Logger logger = server.getLogger();

        if(!server.getPlugin().getServer().getPluginManager().isPluginEnabled(Lagpixel.NAME)){
            logger.warning("Plugin has not been activated at the given time, stopping animation [Firework]");
            return;
        }

        long startTime = System.currentTimeMillis();

        logger.info("Starting [Firework] animation with period: " + lifecycle.getPeriodTime());

        boolean isError = false;

        server.getPlugin().getServer().getPluginManager().registerEvents(this, server.getPlugin());

        while (!((System.currentTimeMillis() - startTime) >= lifecycle.getPeriodTime()) && !isError) {
            if(!server.getPlugin().getServer().getPluginManager().isPluginEnabled(Lagpixel.NAME)) {
               logger.info("Plugin disabled, stopping animation.");
               break;
            }
            for (Player player : server.getPlugin().getServer().getOnlinePlayers()) {
                logger.info("Spawning firework for player: [" + player.getName() + "] at " + player.getLocation());
                if(server.getPlugin().getServer().getPluginManager().isPluginEnabled(Lagpixel.NAME)) {
                    try {
                        Location location = player.getLocation();
                        location.setY(location.getBlockY() + 1.5);
                        server.getPlugin().getServer().getScheduler().runTask(server.getPlugin(), () -> spawnFireworks(location));
                    } catch (Throwable e) {
                        isError = true;
                        e.printStackTrace();
                        logger.warning("Cannot execute [Firework] animation due: " + e.getMessage());
                    }
                }else {
                    logger.info("Plugin disabled, stopping animation");
                }
            }

            try {
                Thread.sleep(800);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        HandlerList.unregisterAll(this);

        logger.info("Stopping [Firework] animation");

    }

    public static void spawnFireworks(Location location){
        Color color = colors[ThreadLocalRandom.current().nextInt(0, colors.length - 1)];
        Firework fw = location.getWorld().spawn(location, Firework.class);
        FireworkMeta fwm = fw.getFireworkMeta();

        fwm.setPower(2);
        fwm.addEffect(FireworkEffect.builder().withColor(color).flicker(true).build());

        fw.setFireworkMeta(fwm);
        fw.detonate();
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Firework) {
            event.setCancelled(true);
        }
    }
}
