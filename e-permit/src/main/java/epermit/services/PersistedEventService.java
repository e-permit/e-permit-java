package epermit.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import epermit.entities.LedgerPersistedEvent;
import epermit.ledgerevents.LedgerEventUtil;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PersistedEventService {
    private final LedgerEventUtil ledgerEventUtil;

    @Transactional
    public void handleReceivedEvent(Map<String, Object> claims, String authorization) {
        Boolean r = ledgerEventUtil.verifyProof(claims, authorization);
        if (!r) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "");
        }
        ledgerEventUtil.handleEvent(claims);
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