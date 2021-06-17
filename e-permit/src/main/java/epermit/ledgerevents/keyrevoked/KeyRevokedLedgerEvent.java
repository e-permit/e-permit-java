package epermit.ledgerevents.keyrevoked;

import epermit.ledgerevents.LedgerEventBase;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KeyRevokedLedgerEvent extends LedgerEventBase {
    private String keyId;

    private Long revokedAt;
}
