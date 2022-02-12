package lgpxl.servercommons.events;

import lgpxl.servercommons.ContextProvider;
import lgpxl.servercommons.events.action.Action;
import lgpxl.servercommons.events.config.Event;
import lgpxl.servercommons.events.config.EventAction;
import lgpxl.servercommons.events.config.EventAnimation;
import lgpxl.servercommons.events.config.EventPreAction;
import lgpxl.servercommons.events.scheduler.Scheduler;
import org.bukkit.plugin.java.JavaPlugin;
import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

class PartyPartyManagerTest implements ContextProvider {
    @Test
    void getEventsTest() throws IOException {
        EventManager eventManager = new EventManager(this);
        List<Event> event = eventManager.getEvents();
        for(Event event1 : event){
            System.out.println(event1.getName());
            System.out.println(event1.getStartTime());
            System.out.println(event1.getEventRules().getKickMessage());
            List<EventAction> list = event1.getEventLifecycle().getActions();
            for(EventAction preAction : event1.getEventPreAction().getPreActions()){
                System.out.println("==============PREACTION==============");
                System.out.println(preAction.getName());
                System.out.println(preAction.getLifecycle().getPeriodTime());
                System.out.println(preAction.getLifecycle().getTimeAfterStart());
                System.out.println(preAction.getOptions());
            }
            for(EventAction list2 : list){
                System.out.println(list2.getName());
                System.out.println(list2.getLifecycle().getPeriodTime());
                System.out.println(list2.getLifecycle().getTimeAfterStart());
                System.out.println(list2.getOptions());
            }
            List<EventAnimation> list3 = event1.getEventLifecycle().getAnimations();
            for(EventAnimation list2 : list3){
                System.out.println(list2.getName());
                System.out.println(list2.getOptions());
            }
            System.out.println(event1.getEndTime());
            System.out.println(event1.getStartTime());
        }
    }

    @Test
    void generateEventsTest() throws IOException, JDOMException {
        File inputFile = new File("events.xml");
        SAXBuilder saxBuilder = new SAXBuilder();
        Document document = saxBuilder.build(inputFile);
        System.out.println("Root element :" + document.getRootElement().getName());
    }

    @Test
    void getActiveEvents() throws IOException {
        EventManager eventManager = new EventManager(null);
        System.out.println(eventManager.getActiveEvents());
    }

    @Test
    void speedTest() throws IOException, InterruptedException {
        for(int i = 0; i < 200; i++) {
            long start = System.currentTimeMillis();
            EventManager eventManager = new EventManager(null);
            List<Event> event = eventManager.getActiveEvents();
            long end = System.currentTimeMillis();
            System.out.println((end - start) / 1000.0);
            Thread.sleep(50);
        }

    }

    @Test
    void test(){
        System.out.println(Runtime.getRuntime().availableProcessors());
    }

    @Override
    public EventManager getEventManager() {
        return null;
    }

    @Override
    public Scheduler getScheduler() {
        return null;
    }

    @Override
    public JavaPlugin getPlugin() {
        return null;
    }

    @Override
    public Logger getLogger() {
        return Logger.getLogger("Lagixel-Test");
    }
}