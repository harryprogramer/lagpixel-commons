package lgpxl.servercommons.events.processor;

public interface EventProcessorManager {
    void setKickRule(boolean kickRule);

    void setKickMessage(String message);
}
