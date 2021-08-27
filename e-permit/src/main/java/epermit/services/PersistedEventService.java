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
import epermit.entities.AuthorityEvent;
import epermit.entities.LedgerPersistedEvent;
import epermit.ledgerevents.LedgerEventUtil;
import epermit.models.enums.PermitType;
import epermit.repositories.AuthorityEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PersistedEventService {
    private final LedgerEventUtil ledgerEventUtil;
    private final AuthorityEventRepository authorityEventRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void handleSendedEvent(String eventId) {
        AuthorityEvent authorityEvent = authorityEventRepository.findByEventId(eventId).get();
        authorityEvent.setSended(true);
        authorityEventRepository.save(authorityEvent);
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
        ledgerEventUtil.handleEvent(claims, proof);
        if (claims.get("event_type").toString().equals("QUOTA_CREATED")) {
            QuotaCreated quotaCreated = new QuotaCreated();
            quotaCreated.setEndNumber((int)claims.get("end_number"));
            quotaCreated.setStartNumber((int)claims.get("start_number"));
            quotaCreated.setPermitYear((int)claims.get("permit_year"));
            quotaCreated.setPermitIssuedFor(claims.get("permit_issued_for").toString());
            quotaCreated.setPermitType((PermitType)claims.get("permit_type"));
            eventPublisher.publishEvent(quotaCreated);
        }
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
