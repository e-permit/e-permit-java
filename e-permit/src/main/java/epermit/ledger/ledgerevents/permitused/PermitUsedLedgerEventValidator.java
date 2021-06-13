package epermit.ledger.ledgerevents.permitused;

import java.util.Map;
import org.springframework.stereotype.Service;
import epermit.ledger.ledgerevents.LedgerEventValidationResult;
import epermit.ledger.ledgerevents.LedgerEventValidator;
import epermit.ledger.repositories.LedgerPermitRepository;
import epermit.ledger.utils.GsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("PERMIT_USED_EVENT_VALIDATOR")
@RequiredArgsConstructor
public class PermitUsedLedgerEventValidator implements LedgerEventValidator {

    private final LedgerPermitRepository permitRepository;

    @Override
    public LedgerEventValidationResult validate(Map<String, Object> payload) {
        PermitUsedLedgerEvent event = GsonUtil.fromMap(payload, PermitUsedLedgerEvent.class);
        if (!permitRepository.existsByPermitId(event.getPermitId())) {
            log.info("PermitUsedEventValidator result is INVALID_PERMITID_OR_ISSUER event is {}",
                    event);
            return LedgerEventValidationResult.fail("INVALID_PERMITID_OR_ISSUER", event);
        }
        log.info("PermitUsedEventValidator result is succeed");
        return LedgerEventValidationResult.success(event);
    }
}

