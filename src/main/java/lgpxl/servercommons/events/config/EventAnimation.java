package lgpxl.servercommons.events.config;

import java.util.HashMap;
import java.util.Map;

public interface EventAnimation {
    String getName();

    EventLifecycle getLifecycle();

    Map<String, String> getOptions();
}
