package epermit.ledgerevents.quotacreated;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.persistence.criteria.Predicate;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import epermit.appevents.QuotaCreated;
import epermit.commons.Check;
import epermit.commons.ErrorCodes;
import epermit.commons.GsonUtil;
import epermit.entities.LedgerQuota;
import epermit.ledgerevents.LedgerEventHandler;
import epermit.models.EPermitProperties;
import epermit.repositories.LedgerQuotaRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("QUOTA_CREATED_EVENT_HANDLER")
@RequiredArgsConstructor
public class QuotaCreatedLedgerEventHandler implements LedgerEventHandler {
    private final LedgerQuotaRepository quotaRepository;
    private final EPermitProperties properties;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @SneakyThrows
    public void handle(Map<String, Object> claims) {
        log.info("QuotaCreatedEventHandler started with {}", claims);
        QuotaCreatedLedgerEvent event = GsonUtil.fromMap(claims, QuotaCreatedLedgerEvent.class);
        Long matched = quotaRepository.count(filterQuotas(event));
        Check.assertTrue(matched == 0, ErrorCodes.INVALID_QUOTA_INTERVAL);
        LedgerQuota quota = new LedgerQuota();
        quota.setActive(true);
        quota.setEndNumber(event.getEndNumber());
        quota.setPermitType(event.getPermitType());
        quota.setStartNumber(event.getStartNumber());
        quota.setPermitYear(event.getPermitYear());
        quota.setPermitIssuer(event.getConsumer());
        quota.setPermitIssuedFor(event.getProducer());
        log.info("QuotaCreatedEventHandler ended with {}", quota);
        quotaRepository.save(quota);
        if (properties.getIssuerCode().equals(quota.getPermitIssuer())) {
            QuotaCreated quotaCreated = new QuotaCreated();
            quotaCreated.setEndNumber(event.getEndNumber());
            quotaCreated.setStartNumber(event.getStartNumber());
            quotaCreated.setPermitYear(event.getPermitYear());
            quotaCreated.setPermitIssuedFor(event.getPermitIssuedFor());
            quotaCreated.setPermitType(event.getPermitType());
            eventPublisher.publishEvent(quotaCreated);
        }
    }

    static Specification<LedgerQuota> filterQuotas(QuotaCreatedLedgerEvent event) {
        Specification<LedgerQuota> spec = (quota, cq, cb) -> {
            List<Predicate> predicates = new ArrayList<Predicate>();
            predicates.add(cb.equal(quota.get("permitIssuer"), event.getPermitIssuer()));
            predicates.add(cb.equal(quota.get("permitIssuedFor"), event.getPermitIssuedFor()));
            predicates.add(cb.equal(quota.get("permitType"), event.getPermitType()));
            predicates.add(cb.equal(quota.get("permitYear"), event.getPermitYear()));
            Predicate startPredicate = cb.between(quota.get("startNumber"), event.getStartNumber(),
                    event.getEndNumber());
            Predicate endPredicate = cb.between(quota.get("endNumber"), event.getStartNumber(),
                    event.getEndNumber());
            predicates.add(cb.or(startPredicate, endPredicate));
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        return spec;
    }
}
