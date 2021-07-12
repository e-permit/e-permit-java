package epermit.ledgerevents;

import java.util.Map;
import java.util.Optional;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import epermit.commons.Check;
import epermit.entities.LedgerPersistedEvent;
import epermit.models.EPermitProperties;
import epermit.repositories.AuthorityRepository;
import epermit.repositories.LedgerPersistedEventRepository;
import epermit.utils.GsonUtil;
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
        if (eventHandler == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "NOT_IMPLEMENTED_EVENT_HANDLER");
        }
        eventHandler.handle(GsonUtil.toMap(event));
        String jws = jwsUtil.createJws(event);
        LedgerPersistedEvent createdEvent = new LedgerPersistedEvent();
        createdEvent.setIssuedFor(event.getEventIssuedFor());
        createdEvent.setEventId(event.getEventId());
        createdEvent.setPreviousEventId(event.getPreviousEventId());
        createdEvent.setEventType(event.getEventType());
        createdEvent.setJws(jws);
        ledgerEventRepository.save(createdEvent);
        String apiUri = authorityRepository.findOneByCode(event.getEventIssuedFor()).getApiUri();
        LedgerEventCreated appEvent = new LedgerEventCreated();
        appEvent.setJws(jws);
        appEvent.setUri(apiUri + "/events");
        eventPublisher.publishEvent(appEvent);
        log.info("Event published {}", appEvent);
    }

    @SneakyThrows
    public LedgerEventHandleResult handleEvent(Map<String, Object> claims) {
        log.info("Event handle started {}", claims);
        LedgerEventBase e = GsonUtil.fromMap(claims, LedgerEventBase.class);
        if (ledgerEventRepository.existsByIssuerAndIssuedForAndEventId(e.getEventIssuer(),
                e.getEventIssuedFor(), e.getEventId())) {
            log.info("Event exists. EventId: {}", e.getEventId());
            return LedgerEventHandleResult.fail("EVENT_EXIST");
        }
        if (e.getPreviousEventId().equals("0")) {
            if (ledgerEventRepository.existsByIssuerAndIssuedFor(e.getEventIssuer(), e.getEventIssuedFor())) {
                return LedgerEventHandleResult.fail("NOTEXIST_PREVIOUSEVENT");
            } else {
                log.info("First event received");
            }
        } else if (!ledgerEventRepository.existsByIssuerAndIssuedForAndEventId(e.getEventIssuer(),
                e.getEventIssuedFor(), e.getPreviousEventId())) {
            return LedgerEventHandleResult.fail("NOTEXIST_PREVIOUSEVENT");
        }

        LedgerEventHandler eventHandler =
                eventHandlers.get(e.getEventType().toString() + "_EVENT_HANDLER");
        if (eventHandler == null) {
            throw new Exception("NOT_IMPLEMENTED_EVENT_HANDLER");
        }
        eventHandler.handle(claims);
        return LedgerEventHandleResult.success();
    }

}
