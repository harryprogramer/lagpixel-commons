package lgpxl.servercommons.events.scheduler;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public interface Scheduler {
    ScheduledFuture<?> scheduleAtFixedRateAsync(LagTask r, long initialDelay,
                                                long period, TimeUnit unit);

    void schedule(LagTask r, long delay, TimeUnit timeUnit);

    void schedule(LagTask r, long delay);

    void schedule(LagTask r);

    void shutdown();
}
