package epermit.services;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.Map;
import javax.transaction.Transactional;
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
    
    @Transactional
    public Map<String, Object> resolveJws(HttpHeaders headers) {
        return jwsUtil.resolveJws(headers);
    }

    @SneakyThrows
    @Transactional
    public EventValidationResult handle(Map<String, Object> claims) {
        EventBase e = GsonUtil.fromMap(claims, EventBase.class);
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
        EventValidationResult r = handle(event.getClaims());
        if (!r.isOk() && r.getErrorCode().equals("NOTEXIST_PREVIOUSEVENT")) {
            EventBase eBase = (EventBase) r.getEvent();
            String url = authorityRepository.findOneByCode(eBase.getIssuer()).getApiUri() + "/events";
            String lastEventId = receivedEventRepository
                    .findTopByIssuerOrderByIdDesc(eBase.getIssuer()).get().getEventId();
            Map<String, String> claims = new HashMap<>();
            claims.put("last_event_id", lastEventId);
            claims.put("issuer", properties.getIssuerCode());
            claims.put("issued_for", eBase.getIssuer());
            String requestJws = jwsUtil.createJws(claims);
            HttpEntity<?> entity = new HttpEntity<>(jwsUtil.getJwsHeader(requestJws));
            String[] jwsList =
                    restTemplate.exchange(url, HttpMethod.GET, entity, String[].class).getBody();
            for (String jws : jwsList) {
                JwsValidationResult jwsValidationResult = jwsUtil.validateJws(jws);
                if (!jwsValidationResult.isValid()) {
                    throw new AccessDeniedException("Jws validation error");
                }
                handle(jwsValidationResult.getPayload());
            }
        }
    }
}
