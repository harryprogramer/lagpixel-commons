package lgpxl.servercommons.events.scheduler;

import lgpxl.servercommons.ContextProvider;
import lgpxl.servercommons.ContextProviderFactory;

import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;

public class LagTask implements Runnable {
    private final Runnable runnable;
    private final int taskId;

    public LagTask(Runnable r){

        //System.out.println("LagTask this " + this + " remote " + r);

        if(r == null) throw new NullPointerException();

        this.runnable = r;
        this.taskId = ThreadLocalRandom.current().nextInt(0, 65565);
    }

    public int getTaskID(){
        return taskId;
    }

    @Override
    public void run() {
        ContextProvider context = ContextProviderFactory.CONTEXT;

        try {
            runnable.run();
        }catch (Throwable t){
            if(context != null){
                context.getLogger().warning("Task ID: " + taskId + " has exception during the execution: " + t.getMessage());
            }
        }
    }
}
