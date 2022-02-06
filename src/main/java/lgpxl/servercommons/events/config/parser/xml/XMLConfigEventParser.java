package lgpxl.servercommons.events.config.parser.xml;

import lgpxl.servercommons.ContextProvider;
import lgpxl.servercommons.events.action.Action;
import lgpxl.servercommons.events.config.*;
import lgpxl.servercommons.events.config.parser.AbstractEventParser;
import lgpxl.servercommons.events.config.parser.builder.EventConfigGenerator;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Logger;

public class XMLConfigEventParser extends AbstractEventParser {
    private final Logger logger;

    public XMLConfigEventParser(ContextProvider contextProvider){
        this.logger = contextProvider.getLogger();
    }

    @Override
    public PartyConfig parseConfig(String text) throws IOException {
        SAXBuilder sax = new SAXBuilder();
        try {
            InputStream targetStream = new ByteArrayInputStream(text.getBytes());
            Document doc = sax.build(targetStream);
            PartyConfig config = buildConfig(doc);
            List<Event> events =  config.getEvents();
            for(Event event : events){
                String name = event.getName();
                for(Event eventToCheck : events){
                    if(eventToCheck.getName().equals(name) && eventToCheck != event){
                        throw new IOException("Duplicate event name for: " + name);
                    }
                }
            }

            return config;
        } catch (JDOMException e) {
            logger.warning("Cannot parse content: " + text);
            throw new IOException(e);
        }
    }

    private static EventRules fetchRules(Element rules){
        List<EventPlayer> blockExceptions = new ArrayList<>();
        List<Element> exceptions = rules.getChild("accessBlockExceptions").getChildren("player");

        for(Element playerElement : exceptions){
            blockExceptions.add(EventConfigGenerator.createPlayer(
                    playerElement.getChildText("username"),
                    playerElement.getChildText("uuid") != null
                            ? UUID.fromString(playerElement.getChildText("uuid")) : null
            ));
        }

        int startWhenPlayersCount = Integer.parseInt(rules.getChildText("startWhenPlayersCount"));
        boolean accessBlock = Boolean.parseBoolean(rules.getChildText("accessBlockBeforeEventEnd"));
        String kickMessage = rules.getChildText("kickMessage");
        boolean kickOnEnd = Boolean.parseBoolean(rules.getChildText("kickOnEnd"));

        return EventConfigGenerator.createRules(blockExceptions, accessBlock, kickMessage, startWhenPlayersCount);
    }

    private static Map<String, String> fetchOptions(Element element){
        Map<String, String> options = new HashMap<>();
        List<Element> optionElements = element.getChild("options").getChildren("setting");

        for(Element setting : optionElements){
            options.put(setting.getAttributeValue("option"),
                    setting.getAttributeValue("value"));
        }

        return options;
    }

    private static EventLifecycle fetchLifecycle(Element element){
        element = element.getChild("lifecycle");
        long startAfter = element.getChildText("startAfter").equals("") ? 0 : Long.parseLong(element.getChildText("startAfter"));
        long periodTime = element.getChildText("periodTime").equals("") ? 0 : Long.parseLong(element.getChildText("periodTime"));

        return EventConfigGenerator.createLifecycle(startAfter, periodTime);
    }

    private static List<EventAction> fetchActions(List<Element> actionElements){
        List<EventAction> actions = new ArrayList<>();
        for(Element actionElement : actionElements){
            actions.add(EventConfigGenerator.createAction(
                    actionElement.getChildText("name"),
                    fetchOptions(actionElement),
                    fetchLifecycle(actionElement)
            ));
        }

        return actions;
    }

    private static EventCourse fetchEventCourse(Element element){
        List<EventAnimation> animations = new ArrayList<>();

        List<Element> animationElements = element.getChild("animations").getChildren("animation");

        for(Element animationElement : animationElements){
            animations.add(EventConfigGenerator.createAnimation(
                    animationElement.getChildText("name"),
                    fetchLifecycle(animationElement),
                    fetchOptions(animationElement)
            ));
        }

        List<EventAction> actions = fetchActions(element.getChild("actions").getChildren("action"));

        return EventConfigGenerator.createCourse(actions, animations);
    }

    private static List<Event> fetchEvents(Element rootNode){
        List<Element> elements = rootNode.getChild("events").getChildren("event");
        List<Event> events = new ArrayList<>();

        for(Element eventElement : elements){
            events.add(
                    EventConfigGenerator.
                            createEvent(eventElement.getChildText("name"),
                                    LocalDateTime.parse(eventElement.getChildText("startAt")),
                                    LocalDateTime.parse(eventElement.getChildText("eventAt")),
                                    LocalDateTime.parse(eventElement.getChildText("endAt")),
                                    fetchRules(eventElement.getChild("rules")),
                                    fetchEventCourse(eventElement.getChild("eventCourse")),
                                    EventConfigGenerator.createPreAction(
                                            fetchActions(
                                                    eventElement.
                                                            getChild("preActions")
                                                            .getChildren("action"))
                                    )
            ));
        }

        return events;
    }


    private static PartyConfig buildConfig(Document doc){
        Element rootNode = doc.getRootElement();
        return EventConfigGenerator.createConfig(
                Boolean.parseBoolean(rootNode.getChildText("isEventsEnabled")),
                fetchEvents(rootNode)
                );
    }
}
