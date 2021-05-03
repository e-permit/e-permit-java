package epermit.events;

import com.google.gson.Gson;
import com.nimbusds.jose.JWSObject;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import epermit.common.EventType;
import epermit.common.JsonUtil;
import epermit.common.JwsValidationResult;
import epermit.events.keycreated.KeyCreatedEvent;
import epermit.events.permitcreated.PermitCreatedEvent;
import epermit.events.permitrevoked.PermitRevokedEvent;
import epermit.events.permitused.PermitUsedEvent;
import epermit.events.quotacreated.QuotaCreatedEvent;
import epermit.repositories.AuthorityRepository;
import epermit.repositories.ReceivedEventRepository;
import epermit.services.KeyService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class EventDispatcher {
    private final ReceivedEventRepository repository;
    private final KeyService keyService;
    private final RestTemplate restTemplate;
    private final SpringEventHandlerRegistry eventHandlerRegistry;

    public EventDispatcher(RestTemplate restTemplate, ReceivedEventRepository repository,
            AuthorityRepository authorityRepository, KeyService keyService,
            SpringEventHandlerRegistry eventHandlerRegistry) {
        this.repository = repository;
        this.restTemplate = restTemplate;
        this.keyService = keyService;
        this.eventHandlerRegistry = eventHandlerRegistry;
    }

    @Transactional
    @SneakyThrows
    public void dispatch(String jws) {
        log.info("The message is recived. The message content is: " + jws);
        Gson gson = JsonUtil.getGson();
        JwsValidationResult r = keyService.validateJws(jws);
        if (r.isValid()) {
            String eventId = JsonUtil.getClaim(jws, "event_id");
            String previousEventId = JsonUtil.getClaim(jws, "previous_event_id");
            Boolean exist = repository.findOneByEventId(eventId).isPresent();
            if (!exist) {
                Boolean previousExist = repository.findOneByEventId(previousEventId).isPresent();
                if (previousExist) {
                    JWSObject jwsObject = JWSObject.parse(jws);
                    String payload = jwsObject.getPayload().toString();
                    Class<?> eventClass = getClassOfEvent(jws);
                    EventHandlerProxy eventHandler = eventHandlerRegistry.getEventHandlers(eventClass.getName()).get(0);
                    Object event = gson.fromJson(payload, eventClass);
                    eventHandler.invoke(event);
                } else {

                }
            }
        }
    }

    private Class<?> getClassOfEvent(String jws) {
        Class<?> eventClass = null;
        EventType eventType = JsonUtil.getClaim(jws, "event_type");
        switch (eventType) {
            case KEY_CREATED:
                eventClass = KeyCreatedEvent.class;
                break;
            case QUOTA_CREATED:
                eventClass = QuotaCreatedEvent.class;
                break;
            case PERMIT_CREATED:
                eventClass = PermitCreatedEvent.class;
                break;
            case PERMIT_REVOKED:
                eventClass = PermitRevokedEvent.class;
                break;
            case PERMIT_USED:
                eventClass = PermitUsedEvent.class;
                break;
            default:
                break;
        }
        return eventClass;
    }

    private void fetch(String eventId) {
        String[] list = restTemplate.getForObject("url", String[].class);

    }
}
