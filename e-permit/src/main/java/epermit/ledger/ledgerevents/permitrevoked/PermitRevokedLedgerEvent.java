package epermit.ledger.ledgerevents.permitrevoked;

import epermit.events.EventBase;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PermitRevokedLedgerEvent extends EventBase {
    private String permitId;
}
