package lgpxl.servercommons.events.action;

import lgpxl.servercommons.ContextProvider;
import lgpxl.servercommons.Lagpixel;
import lgpxl.servercommons.events.config.EventLifecycle;

import java.util.Map;

public class AmbientSoundAction implements Action {
    @Override
    public void runAction(ContextProvider server, EventLifecycle lifecycle, Map<String, String> options) {
        System.out.println("ambient");
    }
}
