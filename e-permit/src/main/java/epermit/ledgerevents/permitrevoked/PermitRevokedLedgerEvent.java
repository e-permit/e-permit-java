package epermit.ledgerevents.permitrevoked;

import epermit.ledgerevents.LedgerEventBase;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PermitRevokedLedgerEvent extends LedgerEventBase {
    private String permitId;
}
