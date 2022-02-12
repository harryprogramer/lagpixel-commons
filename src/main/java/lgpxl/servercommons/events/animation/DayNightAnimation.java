package lgpxl.servercommons.events.animation;

import lgpxl.servercommons.ContextProvider;
import lgpxl.servercommons.events.config.EventLifecycle;
import lgpxl.servercommons.events.scheduler.LagTask;
import org.bukkit.Bukkit;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class DayNightAnimation implements Animation {
    private long previousTime;

    @Override
    public void runAnimation(ContextProvider server, EventLifecycle lifecycle, Map<String, String> options) {
        int seconds = (int) (lifecycle.getPeriodTime() / 1000);
        previousTime = Objects.requireNonNull(Bukkit.getWorld("world")).getFullTime();
        for(int l = 0; l < seconds; l++) {
            for (int i = 0; i < seconds; i++) {
                long finalTime = i * 1000L;
                server.getScheduler().schedule(new LagTask(() -> {
                    server.getPlugin().
                            getServer().getScheduler().runTask(server.getPlugin(),
                                    () -> Objects.requireNonNull(Bukkit.getWorld("world")).setTime(finalTime));
                }), i * 400L, TimeUnit.MILLISECONDS);
            }
        }

        server.getScheduler().schedule(new LagTask(() ->
                server.getPlugin().getServer().getScheduler().
                        runTask(server.getPlugin(), () ->
                                Objects.requireNonNull(Bukkit.getWorld("world")).setFullTime(previousTime))),
                seconds * 1000L, TimeUnit.MILLISECONDS);
    }
}
