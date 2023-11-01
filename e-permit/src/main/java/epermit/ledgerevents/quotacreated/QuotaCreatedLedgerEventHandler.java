package epermit.ledgerevents.quotacreated;

import org.springframework.stereotype.Service;

import epermit.entities.LedgerQuota;
import epermit.ledgerevents.LedgerEventBase;
import epermit.ledgerevents.LedgerEventHandler;
import epermit.repositories.LedgerQuotaRepository;
import epermit.utils.QuotaUtil;
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
        LedgerQuota quota = quotaRepository
                .findOne(QuotaUtil.filterQuotas(event.getEventProducer(), event.getEventConsumer(),
                        event.getPermitType(), event.getPermitYear()))
                .orElse(LedgerQuota.builder().permitIssuer(event.getPermitIssuer())
                        .permitIssuedFor(event.getPermitIssuedFor()).permitType(event.getPermitType())
                        .permitYear(event.getPermitYear()).build());
        quota.setBalance(quota.getBalance() + event.getQuantity());
        quotaRepository.save(quota);
    }
}
