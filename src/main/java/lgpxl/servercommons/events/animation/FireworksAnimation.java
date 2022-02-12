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
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;

public class FireworksAnimation implements Animation, Listener {
    private static final Color[] colors = new Color[]{Color.LIME, Color.BLUE, Color.PURPLE, Color.YELLOW, Color.RED, Color.AQUA, Color.ORANGE, Color.MAROON};

    @Override
    public void runAnimation(ContextProvider server, EventLifecycle lifecycle, Map<String, String> options) {
        Logger logger = server.getLogger();
        logger.info("Start animation fireworks");
        if(!server.getPlugin().isEnabled()){
            logger.warning("Cannot run animation [Firework] because plugin is disabled");
            return;
        }


        if(!server.getPlugin().getServer().getPluginManager().isPluginEnabled(Lagpixel.NAME)){
            logger.warning("Plugin has not been activated at the given time, stopping animation [Firework]");
            return;
        }

        long startTime = System.currentTimeMillis();

        boolean isError = false;

        server.getPlugin().getServer().getPluginManager().registerEvents(this, server.getPlugin());

        while (!((System.currentTimeMillis() - startTime) >= lifecycle.getPeriodTime()) && !isError) {
            if(!server.getPlugin().getServer().getPluginManager().isPluginEnabled(Lagpixel.NAME)) {
               logger.info("Plugin disabled, stopping animation.");
               break;
            }
            for (Player player : server.getPlugin().getServer().getOnlinePlayers()) {
                if(server.getPlugin().getServer().getPluginManager().isPluginEnabled(Lagpixel.NAME)) {
                    try {
                        for(int i = 0; i < 30; i++){
                            server.getPlugin().getServer().getScheduler().runTask(server.getPlugin(), () -> {
                                spawnFireworks(generateRandomLocation(player));
                            });
                        }
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

    private static Location generateRandomLocation(Player player){
        Location previousLocation = player.getLocation();
        double x = ThreadLocalRandom.current().nextDouble(previousLocation.getBlockX() - 150, previousLocation.getBlockX() + 150);
        double z = ThreadLocalRandom.current().nextDouble(previousLocation.getBlockZ() - 150, previousLocation.getBlockZ() + 150);
        double y = previousLocation.getWorld().getHighestBlockYAt((int) x, (int) z) + 25;

        return new Location(previousLocation.getWorld(), x, y, z);
    }

    public static void spawnFireworks(Location location){
        Color color = colors[ThreadLocalRandom.current().nextInt(0, colors.length - 1)];
        Firework fw = location.getWorld().spawn(location, Firework.class);
        FireworkMeta fwm = fw.getFireworkMeta();

        final Random random = new Random();
        final FireworkEffect effect = FireworkEffect.builder().flicker(random.nextBoolean()).
                withColor(getColor(random.nextInt(17) + 1))
                .withFade(getColor(random.nextInt(17) + 1))
                .with(FireworkEffect.Type.values()[random.nextInt(FireworkEffect.Type.values().length)]).
                trail(random.nextBoolean()).build();

        fwm.setPower(1);
        fwm.addEffect(effect);
        fw.setFireworkMeta(fwm);
        fw.detonate();
    }

    private static Color getColor(final int i) {
        return switch (i) {
            case 1 -> Color.AQUA;
            case 2 -> Color.BLACK;
            case 3 -> Color.BLUE;
            case 4 -> Color.FUCHSIA;
            case 5 -> Color.GRAY;
            case 6 -> Color.GREEN;
            case 7 -> Color.LIME;
            case 8 -> Color.MAROON;
            case 9 -> Color.NAVY;
            case 10 -> Color.OLIVE;
            case 11 -> Color.ORANGE;
            case 12 -> Color.PURPLE;
            case 13 -> Color.RED;
            case 14 -> Color.SILVER;
            case 15 -> Color.TEAL;
            case 16 -> Color.WHITE;
            case 17 -> Color.YELLOW;
            default -> null;
        };
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Firework) {
            event.setCancelled(true);
        }
    }
}
