package lgpxl.servercommons.events.processor;

import lgpxl.servercommons.events.action.Action;
import lgpxl.servercommons.events.action.preaction.PreAction;
import lgpxl.servercommons.events.animation.Animation;
import lgpxl.servercommons.events.config.Event;

import java.util.List;
import java.util.Map;

public interface EventProcessor {
    void processEvent(Event event);

    void addAction(String name, Action action);

    void addAnimation(String name, Animation animation);

    void addPreAction(String name, PreAction animation);

    Map<String, Action> getActions();

    Map<String, Animation> getAnimations();

    Map<String, PreAction> getPreActions();

    Event getCurrentEvent();
}
