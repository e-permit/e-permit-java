package epermit.controllers;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
@Slf4j
public class EventController {

    private final 

    @PostMapping()
    public Boolean receiveEvent(@RequestBody Map<String, String> input) {
        log.info("Event is received jws: " + input.get("jws"));
        AppEvent event = new AppEvent();
        event.setJws(input.get("jws"));
        eventPublisher.publishEvent(event);
        return true;
    }

    @GetMapping()
    public List<String> getEvents(String jws) {
        String issuer = "";//JsonUtil.getClaim(jws, "issuer");
        String eventId = ""; //JsonUtil.getClaim(jws, "event_id");
        Optional<CreatedEvent> eventResult =
                eventRepository.findOneByEventIdAndIssuedFor(eventId, issuer);
        if (eventResult.isPresent()) {
            List<CreatedEvent> events =
                    eventRepository.findByIdGreaterThanOrderByIdAsc(eventResult.get().getId());
            return events.stream().map(x -> x.getJws()).collect(Collectors.toList());
        }
        return null;
    }

}
