package epermit.services;

import org.springframework.data.util.Pair;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import java.nio.file.AccessDeniedException;
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
    public EventValidationResult handle(Map<String, Object> claims) {
        log.info("Event handle started {}", claims);
        EventBase e = GsonUtil.fromMap(claims, EventBase.class);
        if (receivedEventRepository.existsByIssuerAndEventId(e.getIssuer(), e.getEventId())) {
            log.info("Event exists. EventId: {}", e.getEventId());
            return EventValidationResult.fail("EVENT_EXIST", e);
        }
        if (!receivedEventRepository.existsByIssuerAndEventId(e.getIssuer(),
                e.getPreviousEventId())) {
            if (receivedEventRepository.existsByIssuer(e.getIssuer())) {
                return EventValidationResult.fail("NOTEXIST_PREVIOUSEVENT", e);
            }else{
                log.info("First event received");
            }
        }
        EventValidator eventValidator =
                eventValidators.get(e.getEventType().toString() + "_EVENT_VALIDATOR");
        if (eventValidator == null) {
            throw new Exception("NOT_IMPLEMENTED_EVENT_VALIDATOR");
        }
        EventValidationResult result = eventValidator.validate(claims);
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
        log.info("Event handle started. Claims is {}", event.getClaims());
        EventValidationResult r = handleOne(event.getClaims());
        if (!r.isOk() && r.getErrorCode().equals("NOTEXIST_PREVIOUSEVENT")) {
            EventBase eBase = (EventBase) r.getEvent();
            log.info("Previous event does not exist. Previous event id: {}", eBase.getPreviousEventId());
            Pair<String, String> pair = getData(eBase.getIssuer());
            String[] jwsList = getEvents(pair.getFirst(), pair.getSecond(), eBase.getIssuer());
            handleAll(jwsList);
        }
    }

    @Transactional
    @SneakyThrows
    private void handleAll(String[] jwsList) {
        for (String jws : jwsList) {
            JwsValidationResult jwsValidationResult = jwsUtil.validateJws(jws);
            if (!jwsValidationResult.isValid()) {
                throw new AccessDeniedException("Jws validation error");
            }
            handle(jwsValidationResult.getPayload());
        }
    }

    @Transactional
    private EventValidationResult handleOne(Map<String, Object> claims) {
        return handle(claims);
    }

    @Transactional
    private Pair<String, String> getData(String issuer) {
        String url = authorityRepository.findOneByCode(issuer).getApiUri() + "/events";
        String lastEventId =
                receivedEventRepository.findTopByIssuerOrderByIdDesc(issuer).get().getEventId();
        return Pair.of(url, lastEventId);
    }

    private String[] getEvents(String url, String lastEventId, String issuer) {
        Map<String, String> claims = new HashMap<>();
        claims.put("last_event_id", lastEventId);
        claims.put("issuer", properties.getIssuerCode());
        claims.put("issued_for", issuer);
        String requestJws = jwsUtil.createJws(claims);
        HttpEntity<?> entity = new HttpEntity<>(jwsUtil.getJwsHeader(requestJws));
        String[] jwsList =
                restTemplate.exchange(url, HttpMethod.GET, entity, String[].class).getBody();
        return jwsList;
    }
}
