package epermit.utils;

import java.util.Map;
import com.nimbusds.jose.JWSObject;
import org.springframework.stereotype.Component;
import epermit.events.EventBase;
import epermit.events.EventHandleResult;
import epermit.events.EventHandler;
import epermit.models.JwsValidationResult;
import epermit.services.EventService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class EventUtil {
    private final JwsUtil jwsUtil;
    private final EventService eventService;
    private final Map<String, EventHandler> eventHandlers;

    @SneakyThrows
    public EventHandleResult handle(String jws) {
        log.info("The message is recived. The message content is: " + jws);
        JwsValidationResult r = jwsUtil.validateJws(jws);
        if (!r.isValid()) {
            return EventHandleResult.fail(r.getErrorCode());
        }
        EventBase e = GsonUtil.getGson().fromJson(JWSObject.parse(jws).getPayload().toString(),
                EventBase.class);
        if(eventService.isReceivedEventExist(e.getIssuer(), e.getEventId())){
            return EventHandleResult.fail("EVENT_EXIST");
        }
        if(eventService.isReceivedEventExist(e.getIssuer(), e.getPreviousEventId())){
            return EventHandleResult.fail("NOTEXIST_PREVIOUSEVENT");
        }
        EventHandler handler = eventHandlers.get(e.getEventType().toString());
        return handler.handle(JWSObject.parse(jws).getPayload().toString());
    }

    
}
