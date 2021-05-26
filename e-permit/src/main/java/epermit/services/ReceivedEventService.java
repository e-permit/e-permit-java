package epermit.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;
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
            return EventValidationResult.jwsFail(r.getErrorCode());
        }
        EventBase e = GsonUtil.fromMap(r.getPayload(), EventBase.class);
        if (receivedEventRepository.existsByIssuerAndEventId(e.getIssuer(), e.getEventId())) {
            return EventValidationResult.fail("EVENT_EXIST", e);
        }
        if (!receivedEventRepository.existsByIssuerAndEventId(e.getIssuer(),
                e.getPreviousEventId())) {
            if (receivedEventRepository.existsByIssuer(e.getIssuer())) {
                return EventValidationResult.fail("NOTEXIST_PREVIOUSEVENT", e);
            }
        }
        EventValidator eventValidator =
                eventValidators.get(e.getEventType().toString() + "_EVENT_VALIDATOR");
        if (eventValidator == null) {
            throw new Exception("NOT_IMPLEMENTED_EVENT_VALIDATOR");
        }
        EventValidationResult result = eventValidator.validate(r.getPayload());
        if (result.isOk()) {
            EventHandler eventHandler =
                    eventHandlers.get(e.getEventType().toString() + "_EVENT_HANDLER");
            if (eventHandler == null) {
                throw new Exception("NOT_IMPLEMENTED_EVENT_HANDLER");
            }
            eventHandler.handle(result.getEvent());
            return EventValidationResult.success(result.getEvent());
        }
        return result;
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
            claims.put("issued_for", eBase.getIssuer());
            String requestJws = jwsUtil.createJws(claims);
            String[] jwsList = restTemplate.getForObject(url + "/" + requestJws, String[].class);
            for (String jws : jwsList) {
                handle(jws);
            }
        }
    }

    /*
     * private ObjectMapper jacksonObjectMapper() { return new
     * ObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)
     * .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false); }
     */
}
