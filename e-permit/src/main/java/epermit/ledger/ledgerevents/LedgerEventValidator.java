package epermit.ledger.ledgerevents;

import java.util.Map;

public interface LedgerEventValidator {
    LedgerEventValidationResult validate(Map<String, Object> claims);
}
