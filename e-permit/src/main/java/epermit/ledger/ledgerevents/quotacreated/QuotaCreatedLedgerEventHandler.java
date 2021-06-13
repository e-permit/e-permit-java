package epermit.ledger.ledgerevents.quotacreated;

import org.springframework.stereotype.Service;
import epermit.ledger.entities.LedgerQuota;
import epermit.ledger.ledgerevents.LedgerEventHandler;
import epermit.ledger.repositories.LedgerQuotaRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("QUOTA_CREATED_EVENT_HANDLER")
@RequiredArgsConstructor
public class QuotaCreatedLedgerEventHandler implements LedgerEventHandler {
    private final LedgerQuotaRepository quotaRepository;
    
    @SneakyThrows
    public void handle(Object e) {
        log.info("QuotaCreatedEventHandler started with {}", e);
        QuotaCreatedLedgerEvent event = (QuotaCreatedLedgerEvent)e;
        LedgerQuota quota = new LedgerQuota();
        quota.setActive(true);
        quota.setEndNumber(event.getEndNumber());
        quota.setPermitType(event.getPermitType());
        quota.setStartNumber(event.getStartNumber());
        quota.setPermitYear(event.getPermitYear());
        log.info("QuotaCreatedEventHandler ended with {}", quota);
        quotaRepository.save(quota);
    }
}
