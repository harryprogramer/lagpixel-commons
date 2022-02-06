package lgpxl.servercommons.events.action.preaction;

import lgpxl.servercommons.ContextProvider;
import lgpxl.servercommons.events.config.Event;
import lgpxl.servercommons.events.config.EventLifecycle;
import lgpxl.servercommons.events.processor.EventProcessorManager;

import java.util.Map;
import java.util.logging.Logger;

public abstract class AbstractPreAction implements PreAction {
    @Override
    public void runAction(ContextProvider server, EventProcessorManager processorManager,
                          Event event, EventLifecycle lifecycle, Map<String, String> options) {

        Logger logger = server.getLogger();

        try{
            onAction(server, processorManager, event, lifecycle, options);
        }catch (Throwable t){
            logger.warning("Cannot execute PreAction  [" + this.getClass().getName() + " ] due: " + t.getMessage());
            t.printStackTrace();
        }
    }

    protected abstract void onAction(ContextProvider server, EventProcessorManager processorManager,
                                      Event event, EventLifecycle lifecycle, Map<String, String> options);
}
