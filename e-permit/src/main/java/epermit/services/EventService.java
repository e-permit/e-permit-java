package epermit.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import epermit.appevents.LedgerEventCreated;
import epermit.appevents.LedgerEventReplay;
import epermit.commons.GsonUtil;
import epermit.entities.Authority;
import epermit.entities.CreatedEvent;
import epermit.entities.LedgerEvent;
import epermit.ledgerevents.LedgerEventUtil;
import epermit.repositories.AuthorityRepository;
import epermit.repositories.CreatedEventRepository;
import epermit.repositories.LedgerEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {
    private final LedgerEventUtil ledgerEventUtil;
    private final CreatedEventRepository createdEventRepository;

    @Transactional
    public void handleSendedEvent(String eventId) {
        CreatedEvent event = createdEventRepository.findByEventId(eventId).get();
        event.setSended(true);
        createdEventRepository.save(event);
    }

    @Transactional
    @SneakyThrows
    public List<LedgerEventReplay> getUnSendedEvents() {
        List<LedgerEventReplay> list = new ArrayList<>();
        List<CreatedEvent> events = createdEventRepository.findAllBySendedFalseOrderByCreatedAtAsc();
        for (CreatedEvent createdEvent : events) {
            LedgerEventReplay appEvent =  new LedgerEventReplay();
            appEvent.setEventCreated(ledgerEventUtil.createAppEvent(createdEvent));
            list.add(appEvent);
        }
        return list;
    }

    @Transactional
    public void handleReceivedEvent(HttpHeaders headers, Object e) {
        log.info("Event claims. {}", e);
        String proof = headers.getFirst(HttpHeaders.AUTHORIZATION);
        log.info("Event jws. {}", proof);
        Boolean r = ledgerEventUtil.verifyProof(e, proof);
        if (!r) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED");
        }
        ledgerEventUtil.handleEvent(GsonUtil.toMap(e), proof);
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
