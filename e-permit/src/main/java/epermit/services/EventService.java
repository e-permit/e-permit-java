package epermit.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import epermit.entities.CreatedEvent;
import epermit.entities.LedgerEvent;
import epermit.ledgerevents.LedgerEventBase;
import epermit.ledgerevents.LedgerEventUtil;
import epermit.models.LedgerEventCreated;
import epermit.repositories.CreatedEventRepository;
import jakarta.persistence.criteria.Predicate;
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
    public void handleSentEvent(String eventId) {
        CreatedEvent event = createdEventRepository.findByEventId(eventId).get();
        event.setSent(true);
        createdEventRepository.save(event);
    }

    @Transactional
    public void handleEventError(String eventId, String error) {
        CreatedEvent event = createdEventRepository.findByEventId(eventId).get();
        event.setError(error);
        createdEventRepository.save(event);
    }

    @SneakyThrows
    public List<LedgerEventCreated> getUnSendedEvents() {
        List<LedgerEventCreated> list = new ArrayList<>();
        List<CreatedEvent> events = createdEventRepository.findAllBySentFalseOrderByCreatedAtAsc();
        for (CreatedEvent createdEvent : events) {
            list.add(ledgerEventUtil.createAppEvent(createdEvent));
        }
        return list;
    }

    @Transactional
    public <T extends LedgerEventBase> void handleReceivedEvent(HttpHeaders headers, T e) {
        String authorization = headers.getFirst(HttpHeaders.AUTHORIZATION);
        log.info("Event jws. {}", authorization);
        String proof = ledgerEventUtil.verifyProof(e, authorization);
        ledgerEventUtil.handleEvent(e, proof);
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