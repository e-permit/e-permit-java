package epermit.ledgerevents.quotacreated;

import java.util.Map;
import org.springframework.stereotype.Service;
import epermit.commons.GsonUtil;
import epermit.entities.LedgerQuota;
import epermit.ledgerevents.LedgerEventHandleResult;
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
    public LedgerEventHandleResult handle(Map<String, Object> claims) {
        log.info("QuotaCreatedEventHandler started with {}", claims);
        QuotaCreatedLedgerEvent event  = GsonUtil.fromMap(claims, QuotaCreatedLedgerEvent.class);
        /*if (authority.getVerifierQuotas().stream()
                .anyMatch(x -> x.isActive() && x.getPermitType() == e.getPermitType()
                        && x.getPermitYear() == e.getPermitYear())) {
            return LedgerEventValidationResult.fail("QUOTA_ALREADY_EXIST", e);
        }*/
        LedgerQuota quota = new LedgerQuota();
        quota.setActive(true);
        quota.setEndNumber(event.getEndNumber());
        quota.setPermitType(event.getPermitType());
        quota.setStartNumber(event.getStartNumber());
        quota.setPermitYear(event.getPermitYear());
        quota.setIssuer(event.getEventIssuedFor());
        quota.setIssuedFor(event.getEventIssuer());
        log.info("QuotaCreatedEventHandler ended with {}", quota);
        quotaRepository.save(quota);
        return LedgerEventHandleResult.success();
    }
}
