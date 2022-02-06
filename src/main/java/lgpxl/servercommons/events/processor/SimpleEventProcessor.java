package lgpxl.servercommons.events.processor;

import lgpxl.servercommons.ContextProvider;
import lgpxl.servercommons.events.action.Action;
import lgpxl.servercommons.events.action.preaction.PreAction;
import lgpxl.servercommons.events.animation.Animation;
import lgpxl.servercommons.events.config.*;
import lgpxl.servercommons.events.config.parser.builder.EventConfigGenerator;
import lgpxl.servercommons.events.scheduler.LagTask;
import lgpxl.servercommons.events.scheduler.Scheduler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

public class SimpleEventProcessor implements EventProcessor, Listener, EventProcessorManager {
    private final List<String> canceledEvents = Collections.synchronizedList(new ArrayList<>());
    private final Map<String, Animation> animations = new ConcurrentHashMap<>();
    private final Map<String, Action> actions = new ConcurrentHashMap<>();
    private final Map<String, PreAction> preActions = new HashMap<>();
    private final ContextProvider context;
    private final Scheduler scheduler;

    /* rules options */
    volatile boolean isKickPlayers = false;
    volatile String kickMessage = null;


    private final AtomicReference<Event> currentRunningEvent = new AtomicReference<>(null);

    private final Logger logger;

    public SimpleEventProcessor(ContextProvider plugin) {
        this.context = plugin;
        this.scheduler = plugin.getScheduler();
        this.logger = plugin.getLogger();
        if(plugin.getPlugin() != null) {
            plugin.getPlugin().getServer().getPluginManager().registerEvents(this, plugin.getPlugin());
        }else {
            logger.info("Missing plugin object detected, this attempt to fire will be treated as local with no real server.");
        }
    }


    @Override
    public void setKickRule(boolean kickRule) {
        isKickPlayers = kickRule;
    }

    @Override
    public void setKickMessage(String message) {
        kickMessage = message;
    }


    private class EventActionTask implements Runnable {
        private final EventAction action;

        public EventActionTask(EventAction action){
            this.action = action;
        }

        @Override
        public void run() {
            Action action = actions.get(this.action.getName());
            if(action != null) {
                action.runAction(context, this.action.getLifecycle(), this.action.getOptions());
            }else {
                logger.warning("Action " + this.action.getName() + " will be skipped due [name error]: cannot find action");
            }
        }
    }

    private class EventPreActionTask implements Runnable {
        private final EventProcessorManager processorManager;
        private final EventAction action;
        private final Event event;

        public EventPreActionTask(EventAction action, Event event, EventProcessorManager processorManager){
            this.processorManager = processorManager;
            this.action = action;
            this.event = event;
        }

        @Override
        public void run() {
            PreAction action = preActions.get(this.action.getName());
            if(action != null) {
                action.runAction(context, processorManager, event,this.action.getLifecycle(), this.action.getOptions());
            }else {
                logger.warning("Action " + this.action.getName() + " will be skipped due [name error]: cannot find action");
            }
        }
    }

    private class EventAnimationTask implements Runnable {
        private final EventAnimation animation;

        public EventAnimationTask(EventAnimation animation){
            this.animation = animation;
        }

        @Override
        public void run() {
            Animation animation = animations.get(this.animation.getName());
            EventLifecycle lifecycle = this.animation.getLifecycle();
            if(lifecycle.getPeriodTime() <= 0){
                long delay = ChronoUnit.MILLIS.between(
                        currentRunningEvent.get().getEventTime(),
                        currentRunningEvent.get().getEndTime()
                );
                lifecycle = EventConfigGenerator.createLifecycle(lifecycle.getTimeAfterStart(),
                        delay);
            }
            if(animation != null){
                animation.runAnimation(context, lifecycle, this.animation.getOptions());
            }else {
                logger.warning("PreAction " + this.animation.getName() + " will be skipped due name error: [cannot find action]");
            }
        }
    }


    private class EventCourseTask implements Runnable {
        private final Event event;

        public EventCourseTask(Event event){
            this.event = event;
        }

        @Override
        public void run() {
            isKickPlayers = false;
            kickMessage = null;

            for(EventAnimation eventAnimation : event.getEventLifecycle().getAnimations()){
                LagTask lagTask = new LagTask(new EventAnimationTask(eventAnimation));
                logger.info("Scheduling work for animation [" + eventAnimation.getName() + "] to event [" + event.getName() + "] with id: " + lagTask.getTaskID());
                scheduler.schedule(lagTask,
                        eventAnimation.getLifecycle().getTimeAfterStart(), TimeUnit.MILLISECONDS);
            }

            for (EventAction eventAction : event.getEventLifecycle().getActions()){
                LagTask lagTask = new LagTask(new EventActionTask(eventAction));
                logger.info("Scheduling work for action [" + eventAction.getName() + "] to event [" + event.getName() + "] with id: " + lagTask.getTaskID());
                scheduler.schedule(new LagTask(new EventActionTask(eventAction)),
                        eventAction.getLifecycle().getTimeAfterStart(), TimeUnit.MILLISECONDS);
            }
        }
    }

    private void schedulePreActions(Event event, long delay){
        for(EventAction action : event.getEventPreAction().getPreActions()){
            if(!(action.getLifecycle().getTimeAfterStart() > delay)) {
                scheduler.schedule(new LagTask(new EventPreActionTask(action, event, this)),
                        delay - action.getLifecycle().getTimeAfterStart(), TimeUnit.MILLISECONDS);
            }else {
                logger.warning("Action [" + action.getName() + "] will not be pre-performed because the time to the event is shorter than the pre-action time");
            }
        }
    }


    private void startEvent(@NotNull Event event){
        logger.info("Starting event " + event.getName());
        prepareEvent(event);
        runEvent(event);
    }

    private void prepareEvent(Event event){
        currentRunningEvent.compareAndSet(null, event);
        isKickPlayers = event.getEventRules().isKickBeforeStart();
        kickMessage = event.getEventRules().getKickMessage();
    }

    private void runEvent(@NotNull Event event){
        LocalDateTime now = LocalDateTime.now();
        long delay = ChronoUnit.MILLIS.between(now, event.getEventTime());
        if(delay <= 0){
            logger.info("Catching up overdue event [" + event.getName() + "] with seconds behind: " + Math.abs(delay / 1000.0) + "s.");
            delay = 0;
        }
        LagTask task = new LagTask(new EventCourseTask(event));
        logger.info("Scheduling course for event [" + event.getName() + "] with id: " + task.getTaskID());
        schedulePreActions(event, delay);
        scheduler.schedule(task, delay, TimeUnit.MILLISECONDS);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerJoin(PlayerLoginEvent event) {
        if (isKickPlayers){
            logger.info("Kicking player [" + event.getPlayer().getName() + "] due event [" + currentRunningEvent.get().getName() + "] rule to kick players before event start.");
            if (kickMessage == null) {
                event.disallow(PlayerLoginEvent.Result.KICK_OTHER, Component
                        .text("Unable to join, some events may block access to the server")
                        .color(TextColor.color(0xFAE731)));
            } else {
                TextComponent textComponent = Component
                        .text(kickMessage)
                        .color(TextColor.color(0xFAE731));
                event.disallow(PlayerLoginEvent.Result.KICK_OTHER ,textComponent);
            }
        }
    }

    @Override
    public void processEvent(Event event) {
        if(event == null)
            throw new IllegalStateException("event to process is null");

        if(currentRunningEvent.get() != null){
            if(currentRunningEvent.get().getName().equals(event.getName())){
                currentRunningEvent.set(event);
                return;
            }
            logger.warning("Cannot run event: " + event.getName() +", another event is currently running: " + currentRunningEvent.get().getName());
            return;
        }

        if(!canceledEvents.isEmpty()) {
            for (String name : canceledEvents) {
                if (!event.getName().equals(name)) {
                    startEvent(event);
                }
            }
        }else {
            startEvent(event);
        }
    }

    @Override
    public Map<String, PreAction> getPreActions() {
        return preActions;
    }

    @Override
    public void addPreAction(String name, PreAction animation) {
        preActions.put(name, animation);
    }

    @Override
    public void addAction(String name, Action action) {
        actions.put(name, action);
    }

    @Override
    public void addAnimation(String name, Animation animation) {
        animations.put(name, animation);
    }

    @Override
    public Map<String, Action> getActions() {
        return null;
    }

    @Override
    public Map<String, Animation> getAnimations() {
        return null;
    }

    @Override
    public Event getCurrentEvent() {
        return currentRunningEvent.get();
    }
}
