package lgpxl.servercommons.events.scheduler;

import lgpxl.servercommons.ContextProvider;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import java.util.logging.Logger;

public class LagTaskScheduler implements Scheduler{
    private static final int MAX_THREADS = 100;
    private final Logger logger;

    public LagTaskScheduler(ContextProvider provider){
        this.asyncTaskExecutor = new ThreadPoolExecutor(0, MAX_THREADS, 60L, TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(), ThreadFactory.INSTANCE);

        this.executor = Executors
                .newSingleThreadScheduledExecutor(ThreadFactory.INSTANCE);
        this.logger = provider.getLogger();
    }

    private final ScheduledExecutorService executor;

    private final ExecutorService asyncTaskExecutor;

    private final Map<Integer, LagTask> tasks = new HashMap<>();

    @Override
    public ScheduledFuture<?> scheduleAtFixedRateAsync(LagTask r, long initialDelay,
                                  long period, TimeUnit unit){
        return executor.scheduleAtFixedRate(r, initialDelay, period, unit);
    }

    @Override
    public void schedule(LagTask r){
        schedule(r, 0, TimeUnit.MILLISECONDS);
    }

    @Override
    public void schedule(LagTask r, long delay, TimeUnit timeUnit) {
        executor.schedule(r, delay, TimeUnit.MILLISECONDS);
    }

    @Override
    public void schedule(LagTask r, long delay) {
        schedule(r, delay, TimeUnit.MILLISECONDS);
    }


    @Override
    public Future<?> executeAsync(LagTask task) {
        if (asyncTaskExecutor instanceof ThreadPoolExecutor) {
            logger.info(
                    "Pool size is now " +
                            ((ThreadPoolExecutor) asyncTaskExecutor).getActiveCount()
            );
        }
        return asyncTaskExecutor.submit(task);
    }

    @Override
    public void shutdown(){
        logger.info("Shutting down executors.");
        executor.shutdownNow();
        asyncTaskExecutor.shutdownNow();
    }
}
