package lgpxl.servercommons;

import lgpxl.servercommons.events.EventManager;
import lgpxl.servercommons.events.action.AmbientSoundAction;
import lgpxl.servercommons.events.action.ScreenTextAction;
import lgpxl.servercommons.events.action.preaction.BlackScreenCounting;
import lgpxl.servercommons.events.animation.FireworksAnimation;
import lgpxl.servercommons.events.scheduler.LagTask;
import lgpxl.servercommons.events.scheduler.LagTaskScheduler;
import lgpxl.servercommons.events.scheduler.Scheduler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

public final class Lagpixel extends JavaPlugin implements ContextProvider {
    private static final String FOLDER_NAME = "LagpixelServer";
    public static final String NAME = "Lagpixel";
    private Logger logger;
    public EventManager eventManager;
    public Scheduler scheduler;

    private final static AtomicInteger errorCounter = new AtomicInteger(0);

    public Lagpixel(){
        this.logger = getLogger();
    }

    public Path getPathOf(String filename){
        return Path.of( FOLDER_NAME + "/" + filename);
    }

    @Override
    public EventManager getEventManager() {
        return eventManager;
    }

    @Override
    public Scheduler getScheduler() {
        return scheduler;
    }

    @Override
    public JavaPlugin getPlugin() {
        return this;
    }

    private void configurePluginFolder(){
        try {
            File file = new File(FOLDER_NAME);
            if (!file.exists()) {
                Files.createDirectory(Path.of(file.getAbsolutePath()));
            }
        }catch (IOException e){
            logger.warning("Cannot create plugin folder directory");
            e.printStackTrace();
        }
    }

    public void init(){
        this.logger = getLogger();
        this.scheduler = new LagTaskScheduler(this);
        this.eventManager = new EventManager(this);

        eventManager.addAction("ScreenText", new ScreenTextAction());
        eventManager.addAction("AmbientSound", new AmbientSoundAction());
        eventManager.addPreAction("BlackScreenCounting", new BlackScreenCounting());
        eventManager.addAnimation("PlayerFireworks", new FireworksAnimation());
    }

    public void startScheduledWork() {
        ScheduledFuture<?> future = scheduler.scheduleAtFixedRateAsync(new LagTask(() -> {
            try {
                eventManager.updateEvents();
            } catch (IOException e) {
                if(errorCounter.getAndIncrement() > 10){
                    logger.warning("Too many errors in loop work, shutting down...");
                    scheduler.shutdown();
                    setEnabled(false);
                    return;
                }
                logger.warning("While scheduled work for [lgpxl-commons] an unknown error has occurred.");
            }
        }), 50, 50, TimeUnit.MILLISECONDS);

        logger.info("Scheduled work started");
    }


    @Override
    public void onEnable() {
        if(ContextProviderFactory.CONTEXT != null)
            ContextProviderFactory.setContext(this);

        logger.info("Available worlds: " + Bukkit.getWorlds());
        configurePluginFolder();
        init();
        startScheduledWork();
    }

    @Override
    public void onDisable() {
        logger.info("Shutting down [lgpxl-commons]");
        logger.info("Stopping scheduler and all scheduled works");
        scheduler.shutdown();
    }
}
