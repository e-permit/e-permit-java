package epermit.ledgerevents.keyrevoked;

import javax.validation.constraints.NotNull;
import epermit.ledgerevents.LedgerEventBase;
import epermit.ledgerevents.LedgerEventType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KeyRevokedLedgerEvent extends LedgerEventBase {
    public KeyRevokedLedgerEvent(String eventIssuer, String eventIssuedFor, String prevEventId) {
        super(eventIssuer, eventIssuedFor, prevEventId, LedgerEventType.KEY_REVOKED);
    }

    @NotNull
    private String keyId;

    @NotNull
    private Long revokedAt;
}
