package epermit.controllers;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import epermit.events.EventInput;
import epermit.services.EventService;
import epermit.events.EventHandleResult;

@RestController
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping()
    public EventHandleResult receiveEvent(@RequestBody EventInput input) {
        return eventService.handle(input.getJws());
    }

    /*
     * @GetMapping("/{eventId}") public List<String> getEvents() { List<CreatedEvent> events =
     * eventRepository.findAll(); return events.stream().map(x ->
     * x.getJws()).collect(Collectors.toList()); }
     */
}
