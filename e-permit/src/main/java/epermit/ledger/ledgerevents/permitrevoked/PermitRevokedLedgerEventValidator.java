package epermit.ledger.ledgerevents.permitrevoked;

import java.util.Map;
import org.springframework.stereotype.Service;
import epermit.ledger.ledgerevents.LedgerEventValidationResult;
import epermit.ledger.ledgerevents.LedgerEventValidator;
import epermit.ledger.repositories.LedgerPermitRepository;
import epermit.ledger.utils.GsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("PERMIT_REVOKED_EVENT_VALIDATOR")
@RequiredArgsConstructor
public class PermitRevokedLedgerEventValidator implements LedgerEventValidator {
    private final LedgerPermitRepository permitRepository;

    @SneakyThrows
    public LedgerEventValidationResult validate(Map<String, Object> payload) {
        log.info("PermitRevokedEventValidator started with {}", payload);
        PermitRevokedLedgerEvent event = GsonUtil.fromMap(payload, PermitRevokedLedgerEvent.class);
        if (!permitRepository.existsByPermitId(event.getPermitId())) {
            log.info("PermitRevokedEventValidator result is INVALID_PERMITID_OR_ISSUER");
            return LedgerEventValidationResult.fail("INVALID_PERMITID_OR_ISSUER", event);
        }
        log.info("PermitRevokedEventValidator result is succeed");
        return LedgerEventValidationResult.success(event);
    }
}

