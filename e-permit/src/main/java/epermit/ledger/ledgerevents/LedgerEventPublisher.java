package epermit.ledger.ledgerevents;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import epermit.ledger.entities.LedgerPersistedEvent;
import epermit.ledger.models.EPermitProperties;
import epermit.ledger.repositories.AuthorityRepository;
import epermit.ledger.repositories.LedgerPersistedEventRepository;
import epermit.ledger.utils.GsonUtil;
import epermit.ledger.utils.JwsUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class LedgerEventPublisher {
    private final EPermitProperties properties;
    private final ApplicationEventPublisher eventPublisher;
    private final JwsUtil jwsUtil;
    private final LedgerPersistedEventRepository ledgerEventRepository;
    private final AuthorityRepository authorityRepository;
    private final Map<String, LedgerEventHandler> eventHandlers;
    private final Map<String, LedgerEventValidator> eventValidators;

    @SneakyThrows
    public <T extends LedgerEventBase> void saveAndPublish(T event, String issuedFor) {
        Optional<LedgerPersistedEvent> lastEventR =
                ledgerEventRepository.findTopByIssuedForOrderByIdDesc(issuedFor);
        event.setEventId(UUID.randomUUID().toString());
        if (lastEventR.isPresent()) {
            event.setPreviousEventId(lastEventR.get().getEventId());
        } else {
            event.setPreviousEventId("0");
            log.info("First event created. Event id is {}", event.getEventId());
        }
        event.setEventTimestamp(Instant.now().getEpochSecond());
        event.setIssuer(properties.getIssuerCode());
        event.setIssuedFor(issuedFor);
        LedgerEventValidator eventValidator =
                eventValidators.get(event.getEventType().toString() + "_EVENT_VALIDATOR");
        if (eventValidator == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "NOT_IMPLEMENTED_EVENT_VALIDATOR");
        }
        Map<String, Object> claims = GsonUtil.toMap(event);
        LedgerEventValidationResult result = eventValidator.validate(claims);
        if (result.isOk()) {
            LedgerEventHandler eventHandler =
                    eventHandlers.get(event.getEventType().toString() + "_EVENT_HANDLER");
            if (eventHandler == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "NOT_IMPLEMENTED_EVENT_HANDLER");
            }
            eventHandler.handle(result.getEvent());
        }
        String jws = jwsUtil.createJws(event);
        LedgerPersistedEvent createdEvent = new LedgerPersistedEvent();
        createdEvent.setIssuedFor(issuedFor);
        createdEvent.setEventId(event.getEventId());
        createdEvent.setPreviousEventId(event.getPreviousEventId());
        createdEvent.setEventType(event.getEventType());
        createdEvent.setJws(jws);
        ledgerEventRepository.save(createdEvent);
        String apiUri = authorityRepository.findOneByCode(issuedFor).getApiUri();
        LedgerEventPublishInput appEvent = new LedgerEventPublishInput();
        appEvent.setJws(jws);
        appEvent.setUri(apiUri + "/events");
        eventPublisher.publishEvent(appEvent);
        log.info("Event published {}", appEvent);
    }

}

