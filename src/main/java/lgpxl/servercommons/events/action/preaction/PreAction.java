package lgpxl.servercommons.events.action.preaction;

import lgpxl.servercommons.ContextProvider;
import lgpxl.servercommons.events.config.Event;
import lgpxl.servercommons.events.config.EventLifecycle;
import lgpxl.servercommons.events.processor.EventProcessorManager;

import java.util.Map;

public interface PreAction {
    /**
     *
     * @param server current context
     * @param lifecycle action lifecycle
     * @param options options for this action
     * @return time to extend the delay
     */
    void runAction(ContextProvider server, EventProcessorManager processorManager, Event event, EventLifecycle lifecycle, Map<String, String> options);
}
