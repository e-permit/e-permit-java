package epermit.services;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import epermit.appevents.LedgerEventCreated;
import epermit.entities.CreatedEvent;
import epermit.entities.LedgerEvent;
import epermit.ledgerevents.LedgerEventBase;
import epermit.ledgerevents.LedgerEventUtil;
import epermit.repositories.CreatedEventRepository;
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
        event.setSent(true);
        createdEventRepository.save(event);
    }

    @Transactional
    @SneakyThrows
    public List<LedgerEventCreated> getUnSendedEvents() {
        List<LedgerEventCreated> list = new ArrayList<>();
        List<CreatedEvent> events = createdEventRepository.findAllBySendedFalseOrderByCreatedAtAsc();
        for (CreatedEvent createdEvent : events) {
            list.add(ledgerEventUtil.createAppEvent(createdEvent));
        }
        return list;
    }

    @Transactional
    public <T extends LedgerEventBase> void handleReceivedEvent(HttpHeaders headers, T e) {
        log.info("Event claims. {}", e);
        ledgerEventUtil.handleEvent(e);
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
