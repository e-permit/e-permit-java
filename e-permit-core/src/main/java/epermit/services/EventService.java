package epermit.services;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.nimbusds.jose.JWSObject;
import org.springframework.stereotype.Component;
import epermit.common.JsonUtil;
import epermit.common.JwsValidationResult;
import epermit.common.PermitProperties;
import epermit.entities.CreatedEvent;
import epermit.events.EventBase;
import epermit.events.EventHandleResult;
import epermit.events.EventHandler;
import epermit.repositories.CreatedEventRepository;
import epermit.repositories.ReceivedEventRepository;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class EventService {
    private final ReceivedEventRepository repository;
    private final KeyService keyService;
    private final Map<String, EventHandler> eventHandlers;
    private final CreatedEventRepository createdEventRepository;
    private final PermitProperties properties;

    public EventService(PermitProperties properties, ReceivedEventRepository repository, KeyService keyService,
            Map<String, EventHandler> eventHandlers, CreatedEventRepository createdEventRepository) {
        this.repository = repository;
        this.keyService = keyService;
        this.eventHandlers = eventHandlers;
        this.createdEventRepository = createdEventRepository;
        this.properties = properties;
    }

    @SneakyThrows
    public EventHandleResult handle(String jws) {
        log.info("The message is recived. The message content is: " + jws);
        JwsValidationResult r = keyService.validateJws(jws);
        if (!r.isValid()) {
            return EventHandleResult.fail("INVALID_JWS");
        }
        String eventId = JsonUtil.getClaim(jws, "event_id");
        String previousEventId = JsonUtil.getClaim(jws, "previous_event_id");
        log.info(eventId);
        log.info(previousEventId);

        Boolean exist = repository.findOneByEventId(eventId).isPresent();
        if (exist) {
            return EventHandleResult.fail("EXIST_EVENT");
        }
        Boolean previousExist = repository.findOneByEventId(previousEventId).isPresent();
        if (!previousExist) {
            return EventHandleResult.fail("NOTEXIST_PREVIOUSEVENT");
        }
        String eventType = JsonUtil.getClaim(jws, "event_type");
        EventHandler handler = eventHandlers.get(eventType);
        return handler.handle(JWSObject.parse(jws).getPayload().toString());
    }

    public <T extends EventBase> void setCommon(T event, String issuedFor) {
        CreatedEvent lastEvent = createdEventRepository.findTopByOrderByIdDesc();
        event.setPreviousEventId(lastEvent.getEventId());
        event.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC).toEpochSecond());
        event.setIssuer(properties.getIssuerCode());
        event.setIssuedFor(issuedFor);
        event.setEventId(UUID.randomUUID().toString());
    }

    public <E extends EventBase> CreatedEvent persist(E event) {
        CreatedEvent entity = new CreatedEvent();
        entity.setIssuedFor(event.getIssuedFor());
        entity.setJws(keyService.createJws(event));
        createdEventRepository.save(entity);
        return entity;
    }
}
