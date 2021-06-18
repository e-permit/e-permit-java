package epermit.ledgerevents.permitused;

import javax.validation.constraints.NotNull;
import epermit.ledgerevents.LedgerEventBase;
import epermit.ledgerevents.LedgerEventType;
import epermit.models.enums.PermitActivityType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PermitUsedLedgerEvent extends LedgerEventBase {

    public PermitUsedLedgerEvent(String issuer, String issuedFor, String prevEventId) {
        super(issuer, issuedFor, prevEventId, LedgerEventType.PERMIT_USED);
    }

    @NotNull
    private String permitId;

    @NotNull
    private PermitActivityType activityType;

    @NotNull
    private Long activityTimestamp;
}
