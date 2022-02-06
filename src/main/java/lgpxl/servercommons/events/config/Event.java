package lgpxl.servercommons.events.config;

import java.time.LocalDateTime;
import java.util.Date;

public interface Event {
    String getName();

    LocalDateTime getStartTime();

    LocalDateTime getEventTime();

    LocalDateTime getEndTime();

    EventRules getEventRules();

    EventCourse getEventLifecycle();

    EventPreAction getEventPreAction();
}
