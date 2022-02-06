package lgpxl.servercommons.events;

import lgpxl.servercommons.ContextProvider;
import lgpxl.servercommons.Lagpixel;
import lgpxl.servercommons.events.action.Action;
import lgpxl.servercommons.events.action.preaction.PreAction;
import lgpxl.servercommons.events.animation.Animation;
import lgpxl.servercommons.events.config.Event;
import lgpxl.servercommons.events.config.parser.EventConfigParser;
import lgpxl.servercommons.events.config.parser.xml.XMLConfigEventParser;
import lgpxl.servercommons.events.processor.EventProcessor;
import lgpxl.servercommons.events.processor.SimpleEventProcessor;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@SuppressWarnings("unused")
public class EventManager {
    public List<String> notifiedEvents = new ArrayList<>();
    private static final String EVENT_FILE = "events.xml";
    private final EventConfigParser configParser;
    private final ContextProvider plugin;
    private final EventProcessor processor;
    private Logger logger;

    public EventManager(ContextProvider plugin){
        this(plugin, new XMLConfigEventParser(plugin), new SimpleEventProcessor(plugin));
        //this.logger = this.plugin.getLogger();
    }
    public EventManager(ContextProvider plugin, EventConfigParser parser, EventProcessor processor){
        this.plugin = plugin;
        this.configParser = parser;
        this.processor = processor;
        this.logger = this.plugin.getLogger();
    }

    private static boolean isWithinRange(LocalDateTime target, LocalDateTime start, LocalDateTime end) {
        return !(target.isBefore(start) || target.isAfter(end));
    }

    public List<Event> getEvents() throws IOException {
        List<Event> eventsToDelete = new ArrayList<>();
        List<Event> events = configParser.parseConfig(Path.of(EVENT_FILE)).getEvents();
        for(Event event : events){
            LocalDateTime startTime = event.getStartTime();
            for(Event eventToCheck : events){
                if(isWithinRange(startTime, eventToCheck.getStartTime(), eventToCheck.getEndTime()) && eventToCheck != event){
                    if(!notifiedEvents.contains(event.getName())) {
                        System.out.println("[put logger here]: Event " + event.getName() + " starts during event " + eventToCheck.getName());
                        System.out.println("[put logger here]: Event " + event.getName() + " gonna be deleted from list due: [deadline violation]");
                        notifiedEvents.add(event.getName());
                        eventsToDelete.add(event);
                    }
                }
            }
        }

        events.removeAll(eventsToDelete);
        return events;
    }

    public List<Event> getActiveEvents() throws IOException {
        List<Event> activeEvents = new ArrayList<>();
        List<Event> events = getEvents();

        for(Event event : events){
            LocalDateTime dateNow = LocalDateTime.now();
            if(event.getStartTime().isBefore(dateNow)){
                if(!event.getEndTime().isBefore(dateNow)) {
                    activeEvents.add(event);
                }
            }
        }

        return activeEvents;
    }

    public void addAnimation(String name, Animation animation){
        processor.addAnimation(name, animation);
    }

    public void addAction(String name, Action action){
        processor.addAction(name, action);
    }

    public void addPreAction(String name, PreAction action){
        processor.addPreAction(name, action);
    }

    public void updateEvents() throws IOException {
        List<Event> activeEvents = getActiveEvents();
        if(!activeEvents.isEmpty()){
            for(Event event : activeEvents){
                processor.processEvent(event);
            }
        }
    }
}
