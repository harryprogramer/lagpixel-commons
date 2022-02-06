package lgpxl.servercommons.events.scheduler;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class ThreadFactory implements java.util.concurrent.ThreadFactory {
    public static final ThreadFactory INSTANCE = new ThreadFactory();
    private static final AtomicInteger counter = new AtomicInteger();

    private ThreadFactory(){
    }

    @Override
    public Thread newThread(@NotNull Runnable r) {
        Thread thread = new Thread(r);
        thread.setName("lgpxl-thread-" + counter.getAndIncrement());
        return thread;
    }
}
