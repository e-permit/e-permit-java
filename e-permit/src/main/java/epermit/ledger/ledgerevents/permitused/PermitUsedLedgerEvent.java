package epermit.ledger.ledgerevents.permitused;

import epermit.ledger.ledgerevents.LedgerEventBase;
import epermit.ledger.models.enums.PermitActivityType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PermitUsedLedgerEvent extends LedgerEventBase {
    private String permitId;

    private PermitActivityType activityType;

    private Long activityTimestamp;
}
