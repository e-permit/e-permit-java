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
        quota.setQuantity(event.getQuantity());
        quota.setPermitType(event.getPermitType());
        quota.setPermitYear(event.getPermitYear());
        quota.setPermitIssuer(event.getEventConsumer());
        quota.setPermitIssuedFor(event.getEventProducer());
        log.info("QuotaCreatedEventHandler ended with {}", quota);
        quotaRepository.save(quota);
    }
}
