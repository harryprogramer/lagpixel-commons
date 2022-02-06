package lgpxl.servercommons.events.config;

import java.util.HashMap;
import java.util.Map;

public interface EventAction {
    String getName();

    Map<String, String> getOptions();

    EventLifecycle getLifecycle();
}
