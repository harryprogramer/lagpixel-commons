import lgpxl.servercommons.ContextProvider;
import lgpxl.servercommons.events.EventManager;
import lgpxl.servercommons.events.action.AmbientSoundAction;
import lgpxl.servercommons.events.action.ScreenTextAction;
import lgpxl.servercommons.events.animation.FireworksAnimation;
import lgpxl.servercommons.events.scheduler.LagTask;
import lgpxl.servercommons.events.scheduler.LagTaskScheduler;
import lgpxl.servercommons.events.scheduler.Scheduler;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

public class LocalPluginTest implements ContextProvider  {
    private static final String FOLDER_NAME = "LagpixelServer";
    private final Logger logger = Logger.getLogger("Lagpixel");


    public final EventManager eventManager;
    public final Scheduler scheduler;

    private final static AtomicInteger errorCounter = new AtomicInteger(0);

    public LocalPluginTest(){
        this.eventManager = new EventManager(this);
        this.scheduler = new LagTaskScheduler(this);
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
        return null;
    }

    @Override
    public Logger getLogger() {
        return logger;
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

    public void initEventManager(){
        eventManager.addAction("ScreenText", new ScreenTextAction());
        eventManager.addAction("AmbientSound", new AmbientSoundAction());
        eventManager.addAnimation("PlayerFireworks", new FireworksAnimation());
    }

    public void startScheduledWork(){
        ScheduledFuture<?> future = scheduler.scheduleAtFixedRateAsync(new LagTask(() -> {
            try {
                eventManager.updateEvents();
            } catch (IOException e) {
                if(errorCounter.getAndIncrement() > 10){
                    logger.warning("Too many errors in loop work, shutting down...");
                    e.printStackTrace();
                    return;
                }
                logger.warning("While scheduled work for [lgpxl-commons] an unknown error has occurred.");
            }
        }), 50, 50, TimeUnit.MILLISECONDS);
        logger.info("Scheduled work started");
        while (!future.isDone()) {

        }
    }


    public void onEnable() {
        configurePluginFolder();
        initEventManager();
        startScheduledWork();
    }


    public void onDisable() {
        logger.info("Shutting down [lgpxl-commons]");
        logger.info("Stopping scheduler and all scheduled works");
        scheduler.shutdown();
    }
    @Test
    void runServer(){
        onEnable();
    }
}
