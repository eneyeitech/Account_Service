package account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.desktop.UserSessionEvent;
import java.util.ArrayList;
import java.util.List;

@Component
public class EventRepository {

    @Autowired
    EventIRepository eventIRepo;

    public Object getEvents() {
        //return  events;
        List<Event> eventList = new ArrayList<>();
        eventIRepo.findAll().forEach(event -> eventList.add(event));
        return eventList;
    }

    public void save(Event event) {
        //saves an event
        Event savedEvent = eventIRepo.save(event);
        System.out.println(savedEvent + " saved.");
    }
}
