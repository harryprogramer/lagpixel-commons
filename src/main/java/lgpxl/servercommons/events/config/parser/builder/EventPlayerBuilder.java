package lgpxl.servercommons.events.config.parser.builder;

import lgpxl.servercommons.events.config.EventPlayer;

import java.util.UUID;

final class EventPlayerBuilder implements EventPlayer {
    private final String username;
    private final UUID uuid;

    EventPlayerBuilder(String username, UUID uuid){
        this.username = username;
        this.uuid = uuid;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public UUID getUUID() {
        return uuid;
    }
}
