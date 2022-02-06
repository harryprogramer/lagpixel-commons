package lgpxl.servercommons.events.config.parser.builder;

import lgpxl.servercommons.events.action.Action;
import lgpxl.servercommons.events.config.EventAction;
import lgpxl.servercommons.events.config.EventPreAction;

import java.util.List;

public class EventPreActionBuilder implements EventPreAction {
    private final List<EventAction> actions;

    public EventPreActionBuilder(List<EventAction> actions){
        this.actions = actions;
    }

    @Override
    public List<EventAction> getPreActions() {
        return actions;
    }
}
