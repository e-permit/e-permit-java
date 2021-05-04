package epermit.services;

import java.util.Map;
import com.nimbusds.jose.JWSObject;
import org.springframework.stereotype.Component;
import epermit.common.JsonUtil;
import epermit.common.JwsValidationResult;
import epermit.events.EventHandleResult;
import epermit.events.EventHandler;
import epermit.repositories.ReceivedEventRepository;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class EventService {
    private final ReceivedEventRepository repository;
    private final KeyService keyService;
    private final Map<String, EventHandler> eventHandlers;

    public EventService(ReceivedEventRepository repository, KeyService keyService,
            Map<String, EventHandler> eventHandlers) {
        this.repository = repository;
        this.keyService = keyService;
        this.eventHandlers = eventHandlers;
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
}
