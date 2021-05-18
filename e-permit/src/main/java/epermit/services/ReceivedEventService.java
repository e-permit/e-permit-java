package epermit.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;
import com.nimbusds.jose.JWSObject;
import epermit.events.EventBase;
import epermit.events.EventValidationResult;
import epermit.events.EventValidator;
import epermit.events.ReceivedAppEvent;
import epermit.models.EPermitProperties;
import epermit.models.results.JwsValidationResult;
import epermit.events.EventHandler;
import epermit.repositories.AuthorityRepository;
import epermit.repositories.ReceivedEventRepository;
import epermit.utils.GsonUtil;
import epermit.utils.JwsUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReceivedEventService {
    private final JwsUtil jwsUtil;
    private final ReceivedEventRepository receivedEventRepository;
    private final Map<String, EventHandler> eventHandlers;
    private final Map<String, EventValidator> eventValidators;
    private final RestTemplate restTemplate;
    private final AuthorityRepository authorityRepository;
    private final EPermitProperties properties;

    @SneakyThrows
    @Transactional
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

    @SneakyThrows
    public void handleReceivedEvent(ReceivedAppEvent event) {
        EventValidationResult r = handle(event.getJws());
        if (!r.isOk() && r.getErrorCode().equals("NOTEXIST_PREVIOUSEVENT")) {
            EventBase eBase = (EventBase) r.getEvent();
            String url = authorityRepository.findOneByCode(eBase.getIssuer()).get().getApiUri();
            String lastEventId = receivedEventRepository
                    .findTopByIssuerOrderByIdDesc(eBase.getIssuer()).get().getEventId();
            Map<String, String> claims = new HashMap<>();
            claims.put("last_event_id", lastEventId);
            claims.put("issuer", properties.getIssuerCode());
            String requestJws = jwsUtil.createJws(claims);
            String[] jwsList = restTemplate.getForObject(url + "/" + requestJws, String[].class);
            for (String jws : jwsList) {
                handle(jws);
            }
        }
    }

}
