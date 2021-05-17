package epermit.utils;

import java.util.Map;
import com.nimbusds.jose.JWSObject;
import org.springframework.stereotype.Component;
import epermit.events.EventBase;
import epermit.events.EventValidationResult;
import epermit.events.EventValidator;
import epermit.events.EventHandler;
import epermit.models.JwsValidationResult;
import epermit.repositories.ReceivedEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class EventUtil {
    private final JwsUtil jwsUtil;
    private final ReceivedEventRepository receivedEventRepository;
    private final Map<String, EventHandler> eventHandlers;
    private final Map<String, EventValidator> eventValidators;

    @SneakyThrows
    public EventValidationResult handle(String jws) {
        log.info("The message is recived. The message content is: " + jws);
        JwsValidationResult r = jwsUtil.validateJws(jws);
        if (!r.isValid()) {
            return EventValidationResult.fail(r.getErrorCode());
        }
        EventBase e = GsonUtil.getGson().fromJson(JWSObject.parse(jws).getPayload().toString(),
                EventBase.class);
        if (receivedEventRepository.existsByIssuerAndEventId(e.getIssuer(), e.getEventId())) {
            return EventValidationResult.fail("EVENT_EXIST");
        }
        if (receivedEventRepository.existsByIssuerAndEventId(e.getIssuer(),
                e.getPreviousEventId())) {
            return EventValidationResult.fail("NOTEXIST_PREVIOUSEVENT");
        }
        EventValidator eventValidator =
                eventValidators.get(e.getEventType().toString() + "_VALIDATOR");
        EventValidationResult result =
                eventValidator.validate(JWSObject.parse(jws).getPayload().toString());
        if (result.isOk()) {
            EventHandler eventHandler = eventHandlers.get(e.getEventType().toString() + "_HANDLER");
            eventHandler.handle(result.getEvent());
        }
        return null;
    }
}
