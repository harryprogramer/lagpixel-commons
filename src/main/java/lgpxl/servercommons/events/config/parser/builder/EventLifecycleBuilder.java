package lgpxl.servercommons.events.config.parser.builder;

import lgpxl.servercommons.events.config.EventLifecycle;

final class EventLifecycleBuilder implements EventLifecycle {
    private final long timeAfterStart;
    private final long periodTime;

    EventLifecycleBuilder(long timeAfterStart, long periodTime){
        this.timeAfterStart = timeAfterStart;
        this.periodTime = periodTime;
    }

    @Override
    public long getTimeAfterStart() {
        return timeAfterStart;
    }

    @Override
    public long getPeriodTime() {
        return periodTime;
    }
}
