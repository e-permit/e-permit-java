package epermit.ledger.ledgerevents;

import lombok.Data;

@Data
public class LedgerEventPublishInput {
    private String uri;
    private String jws;
}

