package epermit.controllers;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import epermit.entities.CreatedEvent;
import epermit.events.AppEvent;
import epermit.repositories.CreatedEventRepository;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/events")
@Slf4j
public class EventController {

    private final ApplicationEventPublisher eventPublisher;
    private final CreatedEventRepository eventRepository;

    public EventController(ApplicationEventPublisher eventPublisher, CreatedEventRepository eventRepository) {
        this.eventPublisher = eventPublisher;
        this.eventRepository = eventRepository;
    }

    @PostMapping()
    public Boolean receiveEvent(@RequestBody Map<String, String> input) {
        log.info("Event is received jws: " + input.get("jws"));
        AppEvent event = new AppEvent();
        event.setJws(input.get("jws"));
        eventPublisher.publishEvent(input);
        return true;
    }

    @GetMapping("/jws")
    public List<String> getEvents(String jws) {
        List<CreatedEvent> events = eventRepository.findAll();
        return events.stream().map(x -> x.getJws()).collect(Collectors.toList());
    }

}
