package epermit.controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import epermit.EventInput;
import epermit.entities.CreatedEvent;
import epermit.repositories.CreatedEventRepository;

@RestController
@RequestMapping("/events")
public class EventController {

    private final ApplicationEventPublisher eventPublisher;
    private final CreatedEventRepository eventRepository;

    public EventController(ApplicationEventPublisher eventPublisher, CreatedEventRepository eventRepository) {
        this.eventPublisher = eventPublisher;
        this.eventRepository = eventRepository;
    }

    @PostMapping()
    public void receiveEvent(@RequestBody EventInput input) {
        eventPublisher.publishEvent(input);
    }

    @GetMapping("/{eventId}")
    public List<String> getEvents() {
        List<CreatedEvent> events = eventRepository.findAll();
        return events.stream().map(x -> x.getJws()).collect(Collectors.toList());
    }
}
