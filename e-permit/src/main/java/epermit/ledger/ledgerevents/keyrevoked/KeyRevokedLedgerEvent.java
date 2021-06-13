package epermit.ledger.ledgerevents.keyrevoked;

import epermit.ledger.ledgerevents.LedgerEventBase;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KeyRevokedLedgerEvent extends LedgerEventBase {
    private String keyId;

    private Long revokedAt;
}
