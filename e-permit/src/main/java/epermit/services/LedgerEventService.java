package epermit.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.persistence.criteria.Predicate;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import epermit.appevents.QuotaCreated;
import epermit.entities.CreatedEvent;
import epermit.entities.LedgerEvent;
import epermit.ledgerevents.LedgerEventHandler;
import epermit.ledgerevents.LedgerEventUtil;
import epermit.models.enums.PermitType;
import epermit.repositories.CreatedEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class LedgerEventService {
    private final LedgerEventUtil ledgerEventUtil;
    private final CreatedEventRepository authorityEventRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final Map<String, LedgerEventHandler> eventHandlers;

    @Transactional
    public void handleSendedEvent(String eventId) {
        CreatedEvent authorityEvent = authorityEventRepository.findByEventId(eventId).get();
        authorityEvent.setSended(true);
        authorityEventRepository.save(authorityEvent);
    }

    @Transactional
    public List<CreatedEvent> getUnSendedEvents() {
        List<CreatedEvent> events = authorityEventRepository.findAll();
        return events;
    }

    @Transactional
    public void handleReceivedEvent(HttpHeaders headers, Map<String, Object> claims) {
        log.info("Event claims. {}", claims);
        String proof = headers.getFirst(HttpHeaders.AUTHORIZATION);
        log.info("Event jws. {}", proof);
        Boolean r = ledgerEventUtil.verifyProof(claims, proof);
        if (!r) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED");
        }
        LedgerEventHandler eventHandler =
                eventHandlers.get(event.getEventType().toString() + "_EVENT_HANDLER");
        Check.isTrue(eventHandler == null, ErrorCodes.INTERNAL_SERVER_ERROR);
        eventHandler.handle(GsonUtil.toMap(event));
        ledgerEventUtil.handleEvent(claims);
        if (claims.get("event_type").toString().equals("QUOTA_CREATED")) {
            QuotaCreated quotaCreated = new QuotaCreated();
            quotaCreated.setEndNumber((int) claims.get("end_number"));
            quotaCreated.setStartNumber((int) claims.get("start_number"));
            quotaCreated.setPermitYear((int) claims.get("permit_year"));
            quotaCreated.setPermitIssuedFor(claims.get("permit_issued_for").toString());
            quotaCreated.setPermitType((PermitType) claims.get("permit_type"));
            eventPublisher.publishEvent(quotaCreated);
        }
    }

    static Specification<LedgerEvent> filterEvents(Long id, String producer, String consumer) {
        Specification<LedgerEvent> spec = (event, cq, cb) -> {
            List<Predicate> predicates = new ArrayList<Predicate>();
            predicates.add(cb.equal(event.get("producer"), producer));
            predicates.add(cb.equal(event.get("consumer"), consumer));
            predicates.add(cb.greaterThan(event.get("id"), id));
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        return spec;
    }
}
