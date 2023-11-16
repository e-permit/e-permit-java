package epermit.ledgerevents;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import epermit.appevents.LedgerEventCreated;
import epermit.commons.EpermitValidationException;
import epermit.commons.ErrorCodes;
import epermit.commons.GsonUtil;
import epermit.entities.Authority;
import epermit.entities.CreatedEvent;
import epermit.entities.LedgerEvent;
import epermit.models.EPermitProperties;
import epermit.repositories.AuthorityRepository;
import epermit.repositories.CreatedEventRepository;
import epermit.repositories.LedgerEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class LedgerEventUtil {
    private final EPermitProperties properties;
    private final ApplicationEventPublisher eventPublisher;
    private final LedgerEventRepository ledgerEventRepository;
    private final AuthorityRepository authorityRepository;
    private final CreatedEventRepository createdEventRepository;
    private final Map<String, LedgerEventHandler> eventHandlers;
    private final RestTemplate restTemplate;

    public String getPreviousEventId(String consumer) {
        String previousEventId = "0";
        Optional<LedgerEvent> lastEventR = ledgerEventRepository.findTopByProducerAndConsumerOrderByCreatedAtDesc(
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
        createdEvent.setSent(false);
        createdEventRepository.save(createdEvent);
        handleEvent(event);
        publishAppEvent(createdEvent);
    }

    public LedgerEventCreated createAppEvent(CreatedEvent createdEvent) {
        LedgerEvent ledgerEvent = ledgerEventRepository.findOneByEventId(createdEvent.getEventId()).get();
        Authority authority = authorityRepository.findOneByCode(ledgerEvent.getConsumer()).get();
        LedgerEventCreated appEvent = new LedgerEventCreated();
        appEvent.setId(UUID.randomUUID());
        appEvent.setEventId(createdEvent.getEventId());
        appEvent.setUri(authority.getApiUri() + "/events/"
                + ledgerEvent.getEventType().name().toLowerCase().replace("_", "-"));
        appEvent.setContent(GsonUtil.toMap(ledgerEvent.getEventContent()));
        return appEvent;
    }

    public void publishAppEvent(CreatedEvent createdEvent) {
        LedgerEventCreated appEvent = createAppEvent(createdEvent);
        eventPublisher.publishEvent(appEvent);
        log.info("Event published {}", appEvent);
    }

    @SneakyThrows
    public <T extends LedgerEventBase> void handleEvent(T e) {
        log.info("Event handle started {}", e);
        Boolean eventExist = ledgerEventRepository.existsByProducerAndConsumerAndEventId(
                e.getEventProducer(), e.getEventConsumer(), e.getEventId());
        if (eventExist)
            throw new EpermitValidationException(ErrorCodes.EVENT_ALREADY_EXISTS);

        if (e.getPreviousEventId().equals("0")) {
            Boolean genesisEventExist = ledgerEventRepository
                    .existsByProducerAndConsumer(e.getEventProducer(), e.getEventConsumer());
            if (genesisEventExist)
                throw new EpermitValidationException(ErrorCodes.GENESIS_EVENT_ALREADY_EXISTS);
            log.info("First event received");
        } else {
            Boolean previousEventExist = ledgerEventRepository.existsByProducerAndConsumerAndEventId(
                    e.getEventProducer(), e.getEventConsumer(), e.getPreviousEventId());
            if (!previousEventExist)
                throw new EpermitValidationException(ErrorCodes.PREVIOUS_EVENT_NOTFOUND);
        }
        LedgerEvent ledgerEvent = new LedgerEvent();
        ledgerEvent.setEventId(e.getEventId());
        ledgerEvent.setConsumer(e.getEventConsumer());
        ledgerEvent.setProducer(e.getEventProducer());
        ledgerEvent.setEventTimestamp(e.getEventTimestamp());
        ledgerEvent.setEventType(e.getEventType());
        ledgerEvent.setPreviousEventId(e.getPreviousEventId());
        ledgerEvent.setEventContent(GsonUtil.getGson().toJson(e));
        ledgerEventRepository.save(ledgerEvent);
        LedgerEventHandler eventHandler = eventHandlers.get(e.getEventType().toString() + "_EVENT_HANDLER");
        eventHandler.handle(e);
    }

    @SneakyThrows
    public ResponseEntity<?> sendEvent(LedgerEventCreated event) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(event.getContent(), headers);
        ResponseEntity<?> result = restTemplate.postForEntity(event.getUri(), request, Object.class);
        return result;
        /*if (result.getStatusCode() != HttpStatus.OK) {
            ApiErrorResponse error = (ApiErrorResponse)result.getBody();
            if(error != null && error.getDetails().get("errorCode").equals("EVENT_ALREADY_EXISTS")){
                
            }
            log.error(GsonUtil.getGson().toJson(result.getBody()));
            return false;
        }
        return true;*/
    }
}
