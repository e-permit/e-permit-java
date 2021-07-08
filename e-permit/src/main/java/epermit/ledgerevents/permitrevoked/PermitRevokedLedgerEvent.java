package epermit.ledgerevents.permitrevoked;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import epermit.ledgerevents.LedgerEventBase;
import epermit.ledgerevents.LedgerEventType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PermitRevokedLedgerEvent extends LedgerEventBase {
    public PermitRevokedLedgerEvent(String eventIssuer, String eventIssuedFor, String prevEventId) {
        super(eventIssuer, eventIssuedFor, prevEventId, LedgerEventType.PERMIT_REVOKED);
    }

    @NotNull
    @Pattern(regexp = "^[A-Z]{2}-[A-Z]{2}-\\d{4}-(1|2|3)-[0-9]+$")
    private String permitId;
}
