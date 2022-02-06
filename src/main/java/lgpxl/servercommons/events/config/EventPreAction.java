package lgpxl.servercommons.events.config;

import lgpxl.servercommons.events.action.Action;

import java.util.List;

public interface EventPreAction {
    List<EventAction> getPreActions();
}
