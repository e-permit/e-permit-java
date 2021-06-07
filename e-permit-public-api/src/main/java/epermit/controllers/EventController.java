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
import epermit.events.ReceivedAppEvent;
import epermit.services.CreatedEventService;
import epermit.utils.JwsUtil;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class EventController {

    private final ApplicationEventPublisher applicationEventPublisher;
    private final CreatedEventService createdEventService;
    private final JwsUtil jwsUtil;

    @PostMapping()
    public Boolean receiveEvent(@RequestHeader HttpHeaders headers) {
        ReceivedAppEvent appEvent = new ReceivedAppEvent();
        Map<String, Object> claims = jwsUtil.resolveJws(headers);
        appEvent.setClaims(claims);
        applicationEventPublisher.publishEvent(appEvent);
        return true;
    }

    @GetMapping()
    public List<String> getEvents(@RequestHeader HttpHeaders headers) {
        Map<String, Object> claims = jwsUtil.resolveJws(headers);
        return createdEventService.getEvents(claims);
    }
}
