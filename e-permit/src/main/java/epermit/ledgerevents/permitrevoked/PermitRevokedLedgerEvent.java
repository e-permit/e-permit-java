package epermit.ledgerevents.permitrevoked;

import javax.validation.constraints.NotNull;
import epermit.ledgerevents.LedgerEventBase;
import epermit.ledgerevents.LedgerEventType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PermitRevokedLedgerEvent extends LedgerEventBase {
    public PermitRevokedLedgerEvent(String issuer, String issuedFor, String prevEventId) {
        super(issuer, issuedFor, prevEventId, LedgerEventType.PERMIT_REVOKED);
    }

    @NotNull
    private String permitId;
}
