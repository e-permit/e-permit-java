package epermit.ledgerevents.quotacreated;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import epermit.commons.EpermitValidationException;
import epermit.commons.ErrorCodes;
import epermit.entities.LedgerQuota;
import epermit.ledgerevents.LedgerEventBase;
import epermit.ledgerevents.LedgerEventHandler;
import epermit.repositories.LedgerQuotaRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("QUOTA_CREATED_EVENT_HANDLER")
@RequiredArgsConstructor
public class QuotaCreatedLedgerEventHandler implements LedgerEventHandler {
    private final LedgerQuotaRepository quotaRepository;

    @Override
    @SneakyThrows
    public <T extends LedgerEventBase> void handle(T claims) {
        log.info("QuotaCreatedEventHandler started with {}", claims);
        QuotaCreatedLedgerEvent event = (QuotaCreatedLedgerEvent) claims;
        Boolean matched = quotaRepository.exists(filterQuotas(event));
        if(matched)
            throw new EpermitValidationException(ErrorCodes.INVALID_QUOTA_INTERVAL);
        LedgerQuota quota = new LedgerQuota();
        quota.setActive(true);
        quota.setEndNumber(event.getEndNumber());
        quota.setPermitType(event.getPermitType());
        quota.setStartNumber(event.getStartNumber());
        quota.setPermitYear(event.getPermitYear());
        quota.setPermitIssuer(event.getEventConsumer());
        quota.setPermitIssuedFor(event.getEventProducer());
        log.info("QuotaCreatedEventHandler ended with {}", quota);
        quotaRepository.save(quota);
    }

    static Specification<LedgerQuota> filterQuotas(QuotaCreatedLedgerEvent event) {
        Specification<LedgerQuota> spec = (quota, cq, cb) -> {
            List<Predicate> predicates = new ArrayList<Predicate>();
            predicates.add(cb.equal(quota.get("permitIssuer"), event.getPermitIssuer()));
            predicates.add(cb.equal(quota.get("permitIssuedFor"), event.getPermitIssuedFor()));
            predicates.add(cb.equal(quota.get("permitType"), event.getPermitType()));
            predicates.add(cb.equal(quota.get("permitYear"), event.getPermitYear()));
            Predicate eventStartPredicate = cb.between(cb.literal(event.getStartNumber()),
                    quota.get("startNumber"), quota.get("endNumber"));
            Predicate eventEndPredicate = cb.between(cb.literal(event.getEndNumber()),
                    quota.get("startNumber"), quota.get("endNumber"));
            Predicate startPredicate = cb.between(quota.get("startNumber"), event.getStartNumber(),
                    event.getEndNumber());
            Predicate endPredicate = cb.between(quota.get("endNumber"), event.getStartNumber(),
                    event.getEndNumber());
            predicates.add(cb.or(eventStartPredicate, eventEndPredicate, startPredicate, endPredicate));
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        return spec;
    }
}
