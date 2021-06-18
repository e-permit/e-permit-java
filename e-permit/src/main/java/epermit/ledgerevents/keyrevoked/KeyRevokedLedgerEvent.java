package epermit.ledgerevents.keyrevoked;

import javax.validation.constraints.NotNull;
import epermit.ledgerevents.LedgerEventBase;
import epermit.ledgerevents.LedgerEventType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KeyRevokedLedgerEvent extends LedgerEventBase {
    public KeyRevokedLedgerEvent(String issuer, String issuedFor, String prevEventId) {
        super(issuer, issuedFor, prevEventId, LedgerEventType.KEY_REVOKED);
    }

    @NotNull
    private String keyId;

    @NotNull
    private Long revokedAt;
}
