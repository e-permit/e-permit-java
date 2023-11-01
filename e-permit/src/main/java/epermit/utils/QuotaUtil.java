package epermit.utils;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import epermit.entities.LedgerQuota;
import epermit.models.enums.PermitType;
import jakarta.persistence.criteria.Predicate;

public class QuotaUtil {
    public static Specification<LedgerQuota> filterQuotas(String iss, String issFor, PermitType pType, Integer pYear) {
        Specification<LedgerQuota> spec = (quota, cq, cb) -> {
            List<Predicate> predicates = new ArrayList<Predicate>();
            predicates.add(cb.equal(quota.get("permitIssuer"), iss));
            predicates.add(cb.equal(quota.get("permitIssuedFor"), issFor));
            predicates.add(cb.equal(quota.get("permitType"), pType));
            predicates.add(cb.equal(quota.get("permitYear"), pYear));
            
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        return spec;
    }
}
