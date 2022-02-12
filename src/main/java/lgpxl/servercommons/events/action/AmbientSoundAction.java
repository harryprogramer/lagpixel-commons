package lgpxl.servercommons.events.action;

import lgpxl.servercommons.ContextProvider;
import lgpxl.servercommons.Lagpixel;
import lgpxl.servercommons.events.config.EventLifecycle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.logging.Logger;

public class AmbientSoundAction implements Action {
    @Override
    public void runAction(ContextProvider server, EventLifecycle lifecycle, Map<String, String> options) {
        if(server.getPlugin().isEnabled()) {
            Logger logger = server.getLogger();
            logger.info("Playing sound: " + options.get("sound"));
            JavaPlugin plugin = server.getPlugin();
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                Sound sound = translateSound(options.get("sound"), server.getLogger());
                if (sound != null) {
                    player.playSound(player.getLocation(), sound, 100000.0f, 1f);
                }
            }
        }
    }

    private static Sound translateSound(String sound, Logger logger){
        switch (sound){
            case "ENTITY_ENDER_DRAGON_DEATH" -> {
                return Sound.ENTITY_ENDER_DRAGON_DEATH;
            }

            case "MUSIC_DISC_OTHERSIDE" -> {
                return Sound.MUSIC_DISC_OTHERSIDE;
            }

            default -> {
                logger.warning("Can't find sound [" + sound + "]");
                return null;
            }
        }
    }
}
