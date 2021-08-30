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
import epermit.appevents.LedgerEventCreated;
import epermit.commons.Check;
import epermit.commons.ErrorCodes;
import epermit.commons.GsonUtil;
import epermit.entities.Authority;
import epermit.entities.CreatedEvent;
import epermit.entities.LedgerEvent;
import epermit.models.EPermitProperties;
import epermit.models.enums.AuthenticationType;
import epermit.repositories.AuthorityRepository;
import epermit.repositories.CreatedEventRepository;
import epermit.repositories.LedgerEventRepository;
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
    private final LedgerEventRepository ledgerEventRepository;
    private final AuthorityRepository authorityRepository;
    private final CreatedEventRepository createdEventRepository;
    private final Map<String, LedgerEventHandler> eventHandlers;

    public String getPreviousEventId(String consumer) {
        String previousEventId = "0";
        Optional<LedgerEvent> lastEventR = ledgerEventRepository
                .findTopByProducerAndConsumerOrderByIdDesc(properties.getIssuerCode(), consumer);
        if (lastEventR.isPresent()) {
            previousEventId = lastEventR.get().getEventId();
        }
        return previousEventId;
    }

    @SneakyThrows
    public <T extends LedgerEventBase> void persistAndPublishEvent(T event) {
        CreatedEvent createdEvent = new CreatedEvent();
        createdEvent.setEventId(event.getEventId());
        createdEvent.setSended(false);
        createdEventRepository.save(createdEvent);
        String proof = createProof(event);
        handleEvent(GsonUtil.toMap(event), proof);
        publishAppEvent(createdEvent);
    }

    public void publishAppEvent(CreatedEvent createdEvent) {
        LedgerEvent ledgerEvent = ledgerEventRepository.findOneByEventId(createdEvent.getEventId()).get();
        Authority authority = authorityRepository.findOneByCode(ledgerEvent.getConsumer());
        LedgerEventCreated appEvent = new LedgerEventCreated();
        appEvent.setEventId(createdEvent.getEventId());
        appEvent.setProofType(authority.getAuthenticationType());
        appEvent.setUri(authority.getApiUri());
        appEvent.setContent(GsonUtil.toMap(ledgerEvent.getEventContent()));
        appEvent.setProof(ledgerEvent.getProof());
        eventPublisher.publishEvent(appEvent);
        log.info("Event published {}", appEvent);
    }

    @SneakyThrows
    public void handleEvent(Map<String, Object> claims, String proof) {
        log.info("Event handle started {}", claims);
        LedgerEventBase e = GsonUtil.fromMap(claims, LedgerEventBase.class);
        Boolean eventExist = ledgerEventRepository.existsByProducerAndConsumerAndEventId(
                e.getProducer(), e.getConsumer(), e.getEventId());
        Check.assertFalse(eventExist, ErrorCodes.EVENT_ALREADY_EXISTS);
        if (e.getPreviousEventId().equals("0")) {
            Boolean genesisEventExist = ledgerEventRepository
                    .existsByProducerAndConsumer(e.getProducer(), e.getConsumer());
            Check.assertFalse(genesisEventExist, ErrorCodes.GENESIS_EVENT_ALREADY_EXISTS);
            log.info("First event received");
        } else {
            Boolean previousEventExist =
                    ledgerEventRepository.existsByProducerAndConsumerAndEventId(e.getProducer(),
                            e.getConsumer(), e.getPreviousEventId());
            Check.assertTrue(previousEventExist, ErrorCodes.PREVIOUS_EVENT_NOTFOUND);
        }
        LedgerEvent ledgerEvent = new LedgerEvent();
        ledgerEvent.setEventId(e.getEventId());
        ledgerEvent.setConsumer(e.getConsumer());
        ledgerEvent.setProducer(e.getProducer());
        ledgerEvent.setEventTimestamp(e.getEventTimestamp());
        ledgerEvent.setEventType(e.getEventType());
        ledgerEvent.setPreviousEventId(e.getPreviousEventId());
        ledgerEvent.setEventContent(GsonUtil.getGson().toJson(claims));
        ledgerEvent.setProof(proof);
        ledgerEventRepository.save(ledgerEvent);
        LedgerEventHandler eventHandler =
                eventHandlers.get(e.getEventType().toString() + "_EVENT_HANDLER");
        eventHandler.handle(GsonUtil.toMap(claims));
    }

    @SneakyThrows
    public <T extends LedgerEventBase> String createProof(T event) {
        Authority authority = authorityRepository.findOneByCode(event.getConsumer());
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
        Authority authority = authorityRepository.findOneByCode(claims.get("producer").toString());
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

}
