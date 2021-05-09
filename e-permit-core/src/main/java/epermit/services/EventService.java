package epermit.services;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.nimbusds.jose.JWSObject;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import epermit.common.JsonUtil;
import epermit.common.JwsValidationResult;
import epermit.common.PermitProperties;
import epermit.entities.CreatedEvent;
import epermit.events.EventBase;
import epermit.events.EventHandleResult;
import epermit.events.EventHandler;
import epermit.repositories.AuthorityRepository;
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
    private final AuthorityRepository authorityRepository;
    private final RestTemplate restTemplate;

    public EventService(ReceivedEventRepository repository, KeyService keyService,
            Map<String, EventHandler> eventHandlers, CreatedEventRepository createdEventRepository,
            AuthorityRepository authorityRepository, RestTemplate restTemplate) {
        this.repository = repository;
        this.keyService = keyService;
        this.eventHandlers = eventHandlers;
        this.createdEventRepository = createdEventRepository;
        this.authorityRepository = authorityRepository;
        this.restTemplate = restTemplate;
    }

    @SneakyThrows
    @Transactional
    public EventHandleResult handle(String jws) {
        log.info("The message is recived. The message content is: " + jws);
        JwsValidationResult r = keyService.validateJws(jws);
        if (!r.isValid()) {
            return EventHandleResult.fail(r.getErrorCode());
        }
        String issuer = JsonUtil.getClaim(jws, "issuer");
        String eventId = JsonUtil.getClaim(jws, "event_id");
        String previousEventId = JsonUtil.getClaim(jws, "previous_event_id");
        Boolean exist = repository.findOneByIssuerAndEventId(issuer, eventId).isPresent();
        if (exist) {
            return EventHandleResult.fail("EXIST_EVENT");
        }
        Boolean previousExist =
                repository.findOneByIssuerAndEventId(issuer, previousEventId).isPresent();
        if (!previousExist) {
            return EventHandleResult.fail("NOTEXIST_PREVIOUSEVENT");
        }
        String eventType = JsonUtil.getClaim(jws, "event_type");
        EventHandler handler = eventHandlers.get(eventType);
        return handler.handle(JWSObject.parse(jws).getPayload().toString());
    }

    public <E extends EventBase> CreatedEvent persist(E event) {
        CreatedEvent entity = new CreatedEvent();
        entity.setIssuedFor(event.getIssuedFor());
        entity.setJws(keyService.createJws(event));
        createdEventRepository.save(entity);
        return entity;
    }

    public List<String> getEvents(String issuer) {
        authorityRepository.findOneByCode(issuer);
        return null;
    }
}
