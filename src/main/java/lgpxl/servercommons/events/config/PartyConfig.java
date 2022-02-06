package lgpxl.servercommons.events.config;

import java.util.List;

public interface PartyConfig {
    boolean isEnableParties();

    List<Event> getEvents();
}
