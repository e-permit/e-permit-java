package epermit.ledgerevents.permitused;

import epermit.ledgerevents.LedgerEventBase;
import epermit.models.enums.PermitActivityType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PermitUsedLedgerEvent extends LedgerEventBase {
    private String permitId;

    private PermitActivityType activityType;

    private Long activityTimestamp;
}
