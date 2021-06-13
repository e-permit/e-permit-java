package epermit.ledger.ledgerevents.quotacreated;

import java.util.Map;
import org.springframework.stereotype.Service;
import epermit.ledger.ledgerevents.LedgerEventValidationResult;
import epermit.ledger.ledgerevents.LedgerEventValidator;
import epermit.ledger.repositories.LedgerQuotaRepository;
import epermit.ledger.utils.GsonUtil;
import lombok.RequiredArgsConstructor;

@Service("QUOTA_CREATED_EVENT_VALIDATOR")
@RequiredArgsConstructor
public class QuotaCreatedLedgerEventValidator implements LedgerEventValidator {
    private final LedgerQuotaRepository quotaRepository;

    @Override
    public LedgerEventValidationResult validate(Map<String, Object> payload) {
        QuotaCreatedLedgerEvent e = GsonUtil.fromMap(payload, QuotaCreatedLedgerEvent.class);
        /*if (authority.getVerifierQuotas().stream()
                .anyMatch(x -> x.isActive() && x.getPermitType() == e.getPermitType()
                        && x.getPermitYear() == e.getPermitYear())) {
            return LedgerEventValidationResult.fail("QUOTA_ALREADY_EXIST", e);
        }*/
        return LedgerEventValidationResult.success(e);
    }

}
