package lgpxl.servercommons.events.config.parser.builder;

import lgpxl.servercommons.events.config.EventAnimation;
import lgpxl.servercommons.events.config.EventLifecycle;

import java.util.HashMap;
import java.util.Map;

final class EventAnimationBuilder implements EventAnimation {
    private final Map<String, String> options;
    private final EventLifecycle lifecycle;
    private final String name;

    EventAnimationBuilder(String name, EventLifecycle lifecycle, Map<String, String> options){
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
