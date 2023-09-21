package epermit.ledgerevents.keyrevoked;

import jakarta.validation.constraints.NotNull;
import epermit.ledgerevents.LedgerEventBase;
import epermit.ledgerevents.LedgerEventType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KeyRevokedLedgerEvent extends LedgerEventBase {
    public KeyRevokedLedgerEvent(String producer, String consumer, String prevEventId) {
        super(producer, consumer, prevEventId, LedgerEventType.KEY_REVOKED);
    }

    @NotNull
    private String keyId;

    @NotNull
    private Long revokedAt;
}
