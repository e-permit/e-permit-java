package epermit.ledgerevents;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import com.nimbusds.jose.JWSObject;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import epermit.commons.Check;
import epermit.commons.ErrorCodes;
import epermit.commons.GsonUtil;
import epermit.entities.Authority;
import epermit.entities.AuthorityEvent;
import epermit.entities.LedgerPersistedEvent;
import epermit.models.EPermitProperties;
import epermit.models.enums.AuthenticationType;
import epermit.repositories.AuthorityRepository;
import epermit.repositories.LedgerPersistedEventRepository;
import epermit.utils.JwsUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class LedgerEventUtil {
    private final EPermitProperties properties;
    private final ApplicationEventPublisher eventPublisher;
    private final JwsUtil jwsUtil;
    private final LedgerPersistedEventRepository ledgerEventRepository;
    private final AuthorityRepository authorityRepository;
    private final Map<String, LedgerEventHandler> eventHandlers;

    public String getPreviousEventId(String issuedFor) {
        String previousEventId = "0";
        Optional<LedgerPersistedEvent> lastEventR = ledgerEventRepository
                .findTopByIssuerAndIssuedForOrderByIdDesc(properties.getIssuerCode(), issuedFor);
        if (lastEventR.isPresent()) {
            previousEventId = lastEventR.get().getEventId();
        }
        return previousEventId;
    }

    @SneakyThrows
    public <T extends LedgerEventBase> void persistAndPublishEvent(T event) {
        handleEvent(GsonUtil.toMap(event));
        String proof = createProof(event);
        String content = GsonUtil.getGson().toJson(event);
        LedgerPersistedEvent createdEvent = new LedgerPersistedEvent();
        createdEvent.setIssuer(event.getEventIssuer());
        createdEvent.setIssuedFor(event.getEventIssuedFor());
        createdEvent.setEventId(event.getEventId());
        createdEvent.setPreviousEventId(event.getPreviousEventId());
        createdEvent.setEventType(event.getEventType());
        createdEvent.setEventContent(content);
        createdEvent.setProof(proof);
        createdEvent.setEventTime(event.getEventTimestamp());
        ledgerEventRepository.save(createdEvent);
        Authority authority = authorityRepository.findOneByCode(event.getEventIssuedFor());
        LedgerEventCreated appEvent = new LedgerEventCreated();
        appEvent.setContent(GsonUtil.toMap(event));
        appEvent.setProof(proof);
        appEvent.setUri(authority.getApiUri() + "/events");
        AuthorityEvent authorityEvent = new AuthorityEvent();
        authorityEvent.setEventId(event.getEventId());
        authority.addEvent(authorityEvent);
        eventPublisher.publishEvent(appEvent);
        log.info("Event published {}", appEvent);
    }

    @SneakyThrows
    public void handleEvent(Map<String, Object> claims) {
        log.info("Event handle started {}", claims);
        LedgerEventBase e = GsonUtil.fromMap(claims, LedgerEventBase.class);
        Boolean eventExist = ledgerEventRepository.existsByIssuerAndIssuedForAndEventId(
                e.getEventIssuer(), e.getEventIssuedFor(), e.getEventId());
        Check.isTrue(eventExist, ErrorCodes.EVENT_ALREADY_EXISTS);
        if (e.getPreviousEventId().equals("0")) {
            Boolean genesisEventExist = ledgerEventRepository
                    .existsByIssuerAndIssuedFor(e.getEventIssuer(), e.getEventIssuedFor());
            Check.isTrue(genesisEventExist, ErrorCodes.GENESIS_EVENT_ALREADY_EXISTS);
            log.info("First event received");
        } else {
            Boolean previousEventExist = ledgerEventRepository.existsByIssuerAndIssuedForAndEventId(
                    e.getEventIssuer(), e.getEventIssuedFor(), e.getPreviousEventId());
            Check.isTrue(!previousEventExist, ErrorCodes.PREVIOUS_EVENT_NOTFOUND);
        }
        LedgerEventHandler eventHandler =
                eventHandlers.get(e.getEventType().toString() + "_EVENT_HANDLER");
        Check.isTrue(eventHandler == null, ErrorCodes.EVENT_ALREADY_EXISTS);
        eventHandler.handle(claims);
    }

    @SneakyThrows
    public <T extends LedgerEventBase> String createProof(T event) {
        Authority authority = authorityRepository.findOneByCode(event.getEventIssuedFor());
        if (authority.getAuthenticationType() == AuthenticationType.BASIC) {
            String proofStr = authority.getCode() + ":" + authority.getApiSecret();
            String proof =
                    Base64.getEncoder().encodeToString(proofStr.getBytes(StandardCharsets.UTF_8));
            return proof;
        } else {
            String jws = jwsUtil.createJws(event);
            JWSObject jwsObject = JWSObject.parse(jws);
            return jwsObject.getParsedParts()[0].decodeToString() + "."
                    + jwsObject.getParsedParts()[1].decodeToString();
        }
    }

    @SneakyThrows
    public Boolean verifyProof(Map<String, Object> claims, String authorization) {
        if (authorization == null) {
            return false;
        }
        Authority authority =
                authorityRepository.findOneByCode(claims.get("event_issuer").toString());
        if (authority.getAuthenticationType() == AuthenticationType.BASIC) {
            if (authorization.toLowerCase().startsWith("basic")) {
                return false;
            }
            String proofB64 = authorization.substring(6);
            String proof = new String(Base64.getDecoder().decode(proofB64), StandardCharsets.UTF_8);
            final String[] values = proof.split(":", 2);
            String authorityCode = values[0];
            String apiSecret = values[1];
            if (!authorityCode.equals(authority.getCode())) {
                return false;
            }
            return apiSecret.equals(authority.getApiSecret());
        } else {
            if (authorization.toLowerCase().startsWith("bearer")) {
                return false;
            }
            String proof = authorization.substring(7);
            String[] proofArr = proof.split(".");
            String payloadJsonStr = GsonUtil.getGson().toJson(claims);
            String payloadBase64 = Base64.getUrlEncoder().encodeToString(payloadJsonStr.getBytes());
            String jws = proofArr[0] + payloadBase64 + proofArr[1];
            return jwsUtil.validateJws(jws);
        }
    }

    public HttpHeaders createEventRequestHeader(AuthenticationType proofType, String proof) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authoriation",
                proofType == AuthenticationType.BASIC ? "Basic " : "Bearer " + proof);
        return headers;
    }

}
