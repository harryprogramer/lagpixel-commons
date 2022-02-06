package lgpxl.servercommons.events.config.parser.builder;

import lgpxl.servercommons.events.config.Event;
import lgpxl.servercommons.events.config.PartyConfig;

import java.util.List;

final class PartyConfigBuilder implements PartyConfig {
    private final boolean isParties;
    private final List<Event> events;

    PartyConfigBuilder(boolean isParties, List<Event> events){
        this.isParties = isParties;
        this.events = events;
    }

    @Override
    public boolean isEnableParties() {
        return isParties;
    }

    @Override
    public List<Event> getEvents() {
        return events;
    }
}
