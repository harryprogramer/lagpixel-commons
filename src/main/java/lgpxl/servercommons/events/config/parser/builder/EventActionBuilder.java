package lgpxl.servercommons.events.config.parser.builder;

import lgpxl.servercommons.events.config.EventAction;
import lgpxl.servercommons.events.config.EventLifecycle;

import java.util.HashMap;
import java.util.Map;

final class EventActionBuilder implements EventAction {
    private final String name;
    private final Map<String, String> options;
    private final EventLifecycle lifecycle;

    EventActionBuilder(String name, Map<String, String> options, EventLifecycle lifecycle){
        this.name = name;
        this.options = options;
        this.lifecycle = lifecycle;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Map<String, String> getOptions() {
        return options;
    }

    @Override
    public EventLifecycle getLifecycle() {
        return lifecycle;
    }
}
