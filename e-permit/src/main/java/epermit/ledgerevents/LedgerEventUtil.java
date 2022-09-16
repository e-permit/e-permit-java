package epermit.ledgerevents;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import org.slf4j.MDC;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import epermit.appevents.LedgerEventCreated;
import epermit.commons.ApiErrorResponse;
import epermit.commons.Check;
import epermit.commons.ErrorCodes;
import epermit.commons.GsonUtil;
import epermit.entities.Authority;
import epermit.entities.CreatedEvent;
import epermit.entities.LedgerEvent;
import epermit.models.EPermitProperties;
import epermit.models.results.VerifyProofResult;
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
        Optional<LedgerEvent> lastEventR =
                ledgerEventRepository.findTopByProducerAndConsumerOrderByCreatedAtDesc(
                        properties.getIssuerCode(), consumer);
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
        String jws = jwsUtil.createJws(event);
        log.info("Created jws length: {}", jws.length());
        String[] parts = jws.split("\\.");
        return parts[0] + "." + parts[2];
    }

    @SneakyThrows
    public VerifyProofResult verifyProof(Object e, String authorization) {
        if (authorization == null) {
            return VerifyProofResult.fail("HEADER_NOTFOUND");
        }
        LedgerEventBase eb = (LedgerEventBase) e;
        Authority authority = authorityRepository.findOneByCode(eb.getEventProducer());
        if (authority == null) {
            return VerifyProofResult.fail("AUTHORITY_NOTFOUND");
        }
        if (!authorization.toLowerCase().startsWith("bearer")) {
            return VerifyProofResult.fail("INVALID_AUTH_TYPE");
        }
        String proof = authorization.substring(7);
        String[] proofArr = proof.split("\\.");
        String payloadJsonStr = GsonUtil.getGson().toJson(e);
        String payloadBase64 =
                Base64.getUrlEncoder().withoutPadding().encodeToString(payloadJsonStr.getBytes());
        String jws = proofArr[0] + "." + payloadBase64 + "." + proofArr[1];
        log.info("Constructed jws: {}", jws);
        log.info("Constructed jws length: {}", jws.length());
        Boolean isValid = jwsUtil.validateJws(jws);
        if (!isValid) {
            return VerifyProofResult.fail("UNAUTHORIZED");
        }
        return VerifyProofResult.success(proof);
    }

    @SneakyThrows
    public Boolean sendEvent(LedgerEventCreated event) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + event.getProof());
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(event.getContent(), headers);
        ResponseEntity<?> result =
                restTemplate.postForEntity(event.getUri(), request, Object.class);
        if (result.getStatusCode() == HttpStatus.ACCEPTED) {
            return true;
        }
        MDC.put("epermitSendError", "SEND_EPERMIT_EVENT_ERROR");
        log.error(GsonUtil.getGson().toJson(result.getBody()));
        MDC.remove("epermitSendError");

        return false;
    }
}
