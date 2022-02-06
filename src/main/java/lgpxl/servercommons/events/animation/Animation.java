package lgpxl.servercommons.events.animation;

import lgpxl.servercommons.ContextProvider;
import lgpxl.servercommons.Lagpixel;
import lgpxl.servercommons.events.config.EventLifecycle;

import java.util.Map;

public interface Animation {
    void runAnimation(ContextProvider server, EventLifecycle lifecycle, Map<String, String> options);
}
