package lgpxl.servercommons.events.config.parser.builder;

import lgpxl.servercommons.events.config.Event;
import lgpxl.servercommons.events.config.EventCourse;
import lgpxl.servercommons.events.config.EventPreAction;
import lgpxl.servercommons.events.config.EventRules;

import java.time.LocalDateTime;

final class EventBuilder implements Event {
    private final String name;
    private final LocalDateTime startTime;
    private final LocalDateTime eventTime;
    private final LocalDateTime endTime;
    private final EventRules rules;
    private final EventCourse course;
    private final EventPreAction preAction;

     EventBuilder(String name, LocalDateTime startTime,
                  LocalDateTime eventTime, LocalDateTime endTime,
                  EventRules rules, EventCourse course, EventPreAction preAction){
        this.name = name;
        this.startTime = startTime;
        this.eventTime = eventTime;
        this.endTime = endTime;
        this.rules = rules;
        this.course = course;
        this.preAction = preAction;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public LocalDateTime getStartTime() {
        return startTime;
    }

    @Override
    public LocalDateTime getEventTime() {
        return eventTime;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    @Override
    public EventRules getEventRules() {
        return rules;
    }

    @Override
    public EventCourse getEventLifecycle() {
        return course;
    }

    @Override
    public EventPreAction getEventPreAction() {
        return preAction;
    }
}
