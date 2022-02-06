package lgpxl.servercommons.events.config.parser.builder;

import lgpxl.servercommons.events.action.Action;
import lgpxl.servercommons.events.config.*;
import org.checkerframework.checker.units.qual.N;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class EventConfigGenerator {

    private EventConfigGenerator(){
        throw new IllegalStateException();
    }

    @Contract(value = "_, _ -> new", pure = true)
    public static @NotNull PartyConfig createConfig(boolean isParties, List<Event> events){
        return new PartyConfigBuilder(isParties, events);
    }

    public static @NotNull Event createEvent(String name, LocalDateTime startTime,
                                             LocalDateTime eventTime, LocalDateTime endTime,
                                             EventRules rules, EventCourse course, EventPreAction preAction){
        return new EventBuilder(name, startTime, eventTime, endTime, rules, course, preAction);
    }

    public static @NotNull EventRulesBuilder createRules(List<EventPlayer> players,
                                                              boolean isKickBeforeStart, String kickMessage,
                                                              int eventStartAtPlayerCount){
        return new EventRulesBuilder(players, isKickBeforeStart, kickMessage, eventStartAtPlayerCount);
    }

    public static @NotNull EventPlayer createPlayer(String username, UUID uuid){
        return new EventPlayerBuilder(username, uuid);
    }

    public static @NotNull EventCourse createCourse(List<EventAction> actions, List<EventAnimation> animations){
        return new EventCourseBuilder(actions, animations);
    }

    public static @NotNull EventAnimation createAnimation(String name, EventLifecycle lifecycle ,Map<String, String> options){
        return new EventAnimationBuilder(name, lifecycle ,options);
    }

    public static @NotNull EventAction createAction(String name, Map<String, String> options, EventLifecycle lifecycle){
        return new EventActionBuilder(name, options, lifecycle);
    }

    public static @NotNull EventLifecycle createLifecycle(long timeAfterStart, long periodTime){
        return new EventLifecycleBuilder(timeAfterStart, periodTime);
    }

    public static @NotNull EventPreAction createPreAction(List<EventAction> actions){
        return new EventPreActionBuilder(actions);
    }
}
