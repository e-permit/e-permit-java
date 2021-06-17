package epermit.controllers;

import java.util.List;
import java.util.Map;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import epermit.ledgerevents.LedgerEventReceived;
import epermit.services.PersistedEventService;
import epermit.utils.JwsUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class EventController {

    private final ApplicationEventPublisher applicationEventPublisher;
    private final PersistedEventService eventService;
    private final JwsUtil jwsUtil;

    @PostMapping()
    public Boolean receiveEvent(@RequestHeader HttpHeaders headers) {
        log.info("Event received. {}", headers.getFirst(HttpHeaders.AUTHORIZATION));
        LedgerEventReceived eventReceived = new LedgerEventReceived();
        Map<String, Object> claims = jwsUtil.resolveJws(headers);
        eventReceived.setClaims(claims);
        applicationEventPublisher.publishEvent(eventReceived);
        log.info("Receive event finished");
        return true;
    }

    @GetMapping()
    public List<String> getEvents(@RequestHeader HttpHeaders headers) {
        log.info("getEvents called. {}", headers.getFirst(HttpHeaders.AUTHORIZATION));
        Map<String, Object> claims = jwsUtil.resolveJws(headers);
        return eventService.getPersistedLedgerEvents(claims);
    }
}
