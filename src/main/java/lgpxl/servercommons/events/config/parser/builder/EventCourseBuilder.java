package lgpxl.servercommons.events.config.parser.builder;

import lgpxl.servercommons.events.config.EventAction;
import lgpxl.servercommons.events.config.EventAnimation;
import lgpxl.servercommons.events.config.EventCourse;

import java.util.List;

final class EventCourseBuilder implements EventCourse {
    private final List<EventAction> eventAction;
    private final List<EventAnimation> eventAnimations;

    EventCourseBuilder(List<EventAction> actions, List<EventAnimation> animations){
        this.eventAction = actions;
        this.eventAnimations = animations;
    }

    @Override
    public List<EventAction> getActions() {
        return eventAction;
    }

    @Override
    public List<EventAnimation> getAnimations() {
        return eventAnimations;
    }
}
