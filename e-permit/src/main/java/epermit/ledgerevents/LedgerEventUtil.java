package epermit.ledgerevents;

import java.util.Map;
import java.util.Optional;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import epermit.commons.Check;
import epermit.commons.ErrorCodes;
import epermit.commons.GsonUtil;
import epermit.entities.LedgerPersistedEvent;
import epermit.models.EPermitProperties;
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
        LedgerEventHandler eventHandler =
                eventHandlers.get(event.getEventType().toString() + "_EVENT_HANDLER");
        Check.isTrue(eventHandler == null, ErrorCodes.INVALID_EVENT);
        eventHandler.handle(GsonUtil.toMap(event));
        String proof = jwsUtil.createJws(event);
        LedgerPersistedEvent createdEvent = new LedgerPersistedEvent();
        createdEvent.setIssuedFor(event.getEventIssuedFor());
        createdEvent.setEventId(event.getEventId());
        createdEvent.setPreviousEventId(event.getPreviousEventId());
        createdEvent.setEventType(event.getEventType());
        createdEvent.setProof(proof);
        ledgerEventRepository.save(createdEvent);
        String apiUri = authorityRepository.findOneByCode(event.getEventIssuedFor()).getApiUri();
        LedgerEventCreated appEvent = new LedgerEventCreated();
        appEvent.setContent(GsonUtil.getGson().toJson(event));
        appEvent.setProof(proof);
        appEvent.setUri(apiUri + "/events");
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
            Check.isTrue(!genesisEventExist, ErrorCodes.GENESIS_EVENT_ALREADY_EXISTS);
            log.info("First event received");
        }
        Boolean previousEventExist = ledgerEventRepository.existsByIssuerAndIssuedForAndEventId(
                e.getEventIssuer(), e.getEventIssuedFor(), e.getPreviousEventId());
        Check.isTrue(!previousEventExist, ErrorCodes.PREVIOUS_EVENT_NOTFOUND);

        LedgerEventHandler eventHandler =
                eventHandlers.get(e.getEventType().toString() + "_EVENT_HANDLER");
        Check.isTrue(eventHandler == null, ErrorCodes.EVENT_ALREADY_EXISTS);
        eventHandler.handle(claims);
    }

}
