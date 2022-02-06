package lgpxl.servercommons.events.action;

import lgpxl.servercommons.ContextProvider;
import lgpxl.servercommons.Lagpixel;
import lgpxl.servercommons.events.config.EventLifecycle;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.logging.Logger;

public class ScreenTextAction implements Action {
    @Override
    public void runAction(ContextProvider server, EventLifecycle lifecycle , Map<String, String> options) {
        final Logger logger = server.getLogger();

        if(server.getPlugin().isEnabled()) {
            for (Player player : server.getPlugin().getServer().getOnlinePlayers()) {
                String title = options.get("text");
                if(title != null) {
                    final Component mainTitle = Component.text(title);
                }else {
                    logger.warning("No text provided for action [ScreenText], skipping.");
                }
            }
        }
    }
}
