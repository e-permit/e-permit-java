package epermit.ledgerevents;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
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
    private final RestTemplate restTemplate;

    public String getPreviousEventId(String consumer) {
        String previousEventId = "0";
        Optional<LedgerEvent> lastEventR = ledgerEventRepository
                .findTopByProducerAndConsumerOrderByCreatedAtDesc(properties.getIssuerCode(), consumer);
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

    public LedgerEventCreated createAppEvent(CreatedEvent createdEvent) {
        LedgerEvent ledgerEvent =
                ledgerEventRepository.findOneByEventId(createdEvent.getEventId()).get();
        Authority authority = authorityRepository.findOneByCode(ledgerEvent.getConsumer());
        LedgerEventCreated appEvent = new LedgerEventCreated();
        appEvent.setEventId(createdEvent.getEventId());
        appEvent.setProofType(authority.getAuthenticationType());
        appEvent.setUri(authority.getApiUri() + "/events/"
                + ledgerEvent.getEventType().name().toLowerCase().replace("_", "-"));
        appEvent.setContent(GsonUtil.toMap(ledgerEvent.getEventContent()));
        appEvent.setProof(ledgerEvent.getProof());
        return appEvent;
    }

    public void publishAppEvent(CreatedEvent createdEvent) {
        LedgerEventCreated appEvent = createAppEvent(createdEvent);
        eventPublisher.publishEvent(appEvent);
        log.info("Event published {}", appEvent);
    }

    @SneakyThrows
    public void handleEvent(Map<String, Object> claims, String proof) {
        log.info("Event handle started {}", claims);
        LedgerEventBase e = GsonUtil.fromMap(claims, LedgerEventBase.class);
        Boolean eventExist = ledgerEventRepository.existsByProducerAndConsumerAndEventId(
                e.getEventProducer(), e.getEventConsumer(), e.getEventId());
        Check.assertFalse(eventExist, ErrorCodes.EVENT_ALREADY_EXISTS);
        if (e.getPreviousEventId().equals("0")) {
            Boolean genesisEventExist = ledgerEventRepository
                    .existsByProducerAndConsumer(e.getEventProducer(), e.getEventConsumer());
            Check.assertFalse(genesisEventExist, ErrorCodes.GENESIS_EVENT_ALREADY_EXISTS);
            log.info("First event received");
        } else {
            Boolean previousEventExist =
                    ledgerEventRepository.existsByProducerAndConsumerAndEventId(
                            e.getEventProducer(), e.getEventConsumer(), e.getPreviousEventId());
            Check.assertTrue(previousEventExist, ErrorCodes.PREVIOUS_EVENT_NOTFOUND);
        }
        LedgerEvent ledgerEvent = new LedgerEvent();
        ledgerEvent.setEventId(e.getEventId());
        ledgerEvent.setConsumer(e.getEventConsumer());
        ledgerEvent.setProducer(e.getEventProducer());
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
        Authority authority = authorityRepository.findOneByCode(event.getEventConsumer());
        if (authority.getAuthenticationType() == AuthenticationType.BASIC) {
            String proofStr = authority.getCode() + ":" + authority.getApiSecret();
            String proof =
                    Base64.getEncoder().encodeToString(proofStr.getBytes(StandardCharsets.UTF_8));
            return proof;
        } else {
            String jws = jwsUtil.createJws(event);
            log.info("Created jws length: {}", jws.length());
            String[] parts = jws.split("\\.");
            return parts[0] + "." + parts[2];
        }
    }

    @SneakyThrows
    public Boolean verifyProof(Object e, String authorization) {
        if (authorization == null) {
            return false;
        }
        LedgerEventBase eb = (LedgerEventBase) e;
        Authority authority = authorityRepository.findOneByCode(eb.getEventProducer());
        if (authority == null) {
            return false;
        }
        if (authority.getAuthenticationType() == AuthenticationType.BASIC) {
            if (!authorization.toLowerCase().startsWith("basic")) {
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
            if (!authorization.toLowerCase().startsWith("bearer")) {
                return false;
            }
            String proof = authorization.substring(7);
            String[] proofArr = proof.split("\\.");
            String payloadJsonStr = GsonUtil.getGson().toJson(e);
            String payloadBase64 = Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(payloadJsonStr.getBytes());
            String jws = proofArr[0] + "." + payloadBase64 + "." + proofArr[1];
            log.info("Constructed jws: {}", jws);
            log.info("Constructed jws length: {}", jws.length());
            return jwsUtil.validateJws(jws);
        }
    }

    @SneakyThrows
    public LedgerEventResult sendEvent(LedgerEventCreated event) {
        HttpHeaders headers = createEventRequestHeader(event.getProofType(), event.getProof());
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(event.getContent(), headers);
        LedgerEventResult result =
                restTemplate.postForObject(event.getUri(), request, LedgerEventResult.class);
        return result;
    }

    private HttpHeaders createEventRequestHeader(AuthenticationType proofType, String proof) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization",
                proofType == AuthenticationType.BASIC ? "Basic " : "Bearer " + proof);
        return headers;
    }
}
