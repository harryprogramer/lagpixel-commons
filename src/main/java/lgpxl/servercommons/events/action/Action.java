package lgpxl.servercommons.events.action;

import lgpxl.servercommons.ContextProvider;
import lgpxl.servercommons.Lagpixel;
import lgpxl.servercommons.events.config.EventLifecycle;

import java.util.Map;

public interface Action {

    void runAction(ContextProvider server, EventLifecycle lifecycle, Map<String, String> options);
}
