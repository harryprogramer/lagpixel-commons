package lgpxl.servercommons.events.config.parser.builder;

import lgpxl.servercommons.events.config.EventPlayer;
import lgpxl.servercommons.events.config.EventRules;

import java.util.List;

final class EventRulesBuilder implements EventRules  {
    private final List<EventPlayer> players;
    private final boolean isKickBeforeStart;
    private final String kickMessage;
    private final int eventStartAtPlayerCount;

    EventRulesBuilder(List<EventPlayer> players,
                      boolean isKickBeforeStart, String kickMessage,
                      int eventStartAtPlayerCount){
        this.eventStartAtPlayerCount = eventStartAtPlayerCount;
        this.isKickBeforeStart = isKickBeforeStart;
        this.kickMessage = kickMessage;
        this.players = players;
    }

    @Override
    public List<EventPlayer> getPlayerBlockExceptions() {
        return players;
    }

    @Override
    public boolean isKickBeforeStart() {
        return isKickBeforeStart;
    }

    @Override
    public String getKickMessage() {
        return kickMessage;
    }

    @Override
    public int startEventAtPlayerCount() {
        return eventStartAtPlayerCount;
    }
}
