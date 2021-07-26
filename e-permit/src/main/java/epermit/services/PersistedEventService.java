package epermit.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import epermit.commons.Check;
import epermit.commons.ErrorCodes;
import epermit.entities.LedgerPersistedEvent;
import epermit.ledgerevents.LedgerEventUtil;
import epermit.models.EPermitProperties;
import epermit.repositories.LedgerPersistedEventRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PersistedEventService {
    private final LedgerEventUtil ledgerEventUtil;
    private final LedgerPersistedEventRepository eventRepository;
    private final EPermitProperties properties;

    @Transactional
    public void handleReceivedEvent(Map<String, Object> claims, String authorization) {
        Boolean r = ledgerEventUtil.verifyProof(claims, authorization);
        if(!r){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "");
        }
        ledgerEventUtil.handleEvent(claims);
    }

    @Transactional
    public List<LedgerPersistedEvent> getUnsentEvents(String issuedFor, String eventId) {
        LedgerPersistedEvent event = eventRepository.findOneByEventId(eventId).get();
        List<LedgerPersistedEvent> events = eventRepository
                .findAll(filterEvents(event.getId(), properties.getIssuerCode(), issuedFor));
        return events;
    }

    @Transactional
    public String getLastReceivedEventId(String issuer) {
        Optional<LedgerPersistedEvent> e = eventRepository
                .findTopByIssuerAndIssuedForOrderByIdDesc(issuer, properties.getIssuerCode());
        return e.get().getEventId();
    }

    static Specification<LedgerPersistedEvent> filterEvents(Long id, String issuer,
            String issuedFor) {
        Specification<LedgerPersistedEvent> spec = (event, cq, cb) -> {
            List<Predicate> predicates = new ArrayList<Predicate>();
            predicates.add(cb.equal(event.get("issuer"), issuer));
            predicates.add(cb.equal(event.get("issued_for"), issuedFor));
            predicates.add(cb.greaterThan(event.get("id"), id));
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        return spec;
    }
}

