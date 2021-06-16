package epermit.ledger.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import epermit.ledger.entities.LedgerPersistedEvent;
import epermit.ledger.ledgerevents.LedgerEventBase;
import epermit.ledger.ledgerevents.LedgerEventHandleResult;
import epermit.ledger.ledgerevents.LedgerEventUtil;
import epermit.ledger.models.EPermitProperties;
import epermit.ledger.models.results.JwsValidationResult;
import epermit.ledger.repositories.AuthorityRepository;
import epermit.ledger.repositories.LedgerPersistedEventRepository;
import epermit.ledger.utils.GsonUtil;
import epermit.ledger.utils.JwsUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PersistedEventService {

    private final JwsUtil jwsUtil;
    private final LedgerEventUtil ledgerEventUtil;
    private final RestTemplate restTemplate;
    private final EPermitProperties properties;
    private final AuthorityRepository authorityRepository;
    private final LedgerPersistedEventRepository eventRepository;

    @Transactional
    @SneakyThrows
    private void handleAll(String[] jwsList) {
        for (String jws : jwsList) {
            JwsValidationResult jwsValidationResult = jwsUtil.validateJws(jws);
            if (!jwsValidationResult.isValid()) {
                throw new AccessDeniedException("Jws validation error");
            }
            ledgerEventUtil.handleEvent(jwsValidationResult.getPayload());
        }
    }

    @Transactional
    private LedgerEventHandleResult handleOne(Map<String, Object> claims) {
        return ledgerEventUtil.handleEvent(claims);
    }

    @Transactional
    private Pair<String, String> getUrlAndJws(String issuedFor) {
        String url = authorityRepository.findOneByCode(issuedFor).getApiUri() + "/events";
        String lastEventId = eventRepository
                .findTopByIssuerAndIssuedForOrderByIdDesc(properties.getIssuerCode(), issuedFor)
                .get().getEventId();
        Map<String, String> claims = new HashMap<>();
        claims.put("last_event_id", lastEventId);
        claims.put("issuer", properties.getIssuerCode());
        claims.put("issued_for", issuedFor);
        String requestJws = jwsUtil.createJws(claims);
        return Pair.of(url, requestJws);
    }

    @SneakyThrows
    public void handleLedgerEventReceived(Map<String, Object> eventClaims) {
        log.info("Event handle started. Claims is {}", eventClaims);
        LedgerEventHandleResult r = handleOne(eventClaims);
        if (!r.isOk() && r.getErrorCode().equals("NOTEXIST_PREVIOUSEVENT")) {
            LedgerEventBase eBase = GsonUtil.fromMap(eventClaims, LedgerEventBase.class);
            log.info("Previous event does not exist. Previous event id: {}",
                    eBase.getPreviousEventId());
            Pair<String, String> pair = getUrlAndJws(eBase.getIssuer());
            HttpEntity<?> entity = new HttpEntity<>(jwsUtil.getJwsHeader(pair.getSecond()));
            String[] jwsList = restTemplate
                    .exchange(pair.getFirst(), HttpMethod.GET, entity, String[].class).getBody();
            handleAll(jwsList);
        }
    }

    @SneakyThrows
    public List<String> getPersistedLedgerEvents(Map<String, Object> claims) {
        String issuer = (String) claims.get("issuer");
        String issuedFor = (String) claims.get("issued_for");
        String lastEventId = (String) claims.get("last_event_id");
        Optional<LedgerPersistedEvent> eventR = eventRepository
                .findOneByIssuerAndIssuedForAndEventId(issuer, lastEventId, issuedFor);
        if (eventR.isPresent()) {
            List<LedgerPersistedEvent> createdEvents =
                    eventRepository.findByIdGreaterThanOrderByIdAsc(eventR.get().getId());
            List<String> events =
                    createdEvents.stream().map(x -> x.getJws()).collect(Collectors.toList());
            return events;
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "INVALID_EVENT_ID");
    }
}
