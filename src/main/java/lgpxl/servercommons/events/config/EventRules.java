package lgpxl.servercommons.events.config;

import org.bukkit.entity.Player;

import java.util.List;

public interface EventRules {
    List<EventPlayer> getPlayerBlockExceptions();

    boolean isKickBeforeStart();

    String getKickMessage();

    /**
     * @return -1 if ignore
     */
    int startEventAtPlayerCount();
}
