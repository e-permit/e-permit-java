package epermit.ledger.ledgerevents;

import java.util.Map;
import lombok.Data;

@Data
public class LedgerEventReceived {
    private Map<String, Object> claims;
}

