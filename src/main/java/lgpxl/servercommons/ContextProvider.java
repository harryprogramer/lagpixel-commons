package lgpxl.servercommons;

import lgpxl.servercommons.events.EventManager;
import lgpxl.servercommons.events.scheduler.Scheduler;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public interface ContextProvider {
    EventManager getEventManager();

    Scheduler getScheduler();

    JavaPlugin getPlugin();

    Logger getLogger();
}
