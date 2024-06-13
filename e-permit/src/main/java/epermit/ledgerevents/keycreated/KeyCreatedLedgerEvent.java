package epermit.ledgerevents.keycreated;

import jakarta.validation.constraints.NotNull;
import epermit.ledgerevents.LedgerEventBase;
import epermit.ledgerevents.LedgerEventType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KeyCreatedLedgerEvent extends LedgerEventBase {
    public KeyCreatedLedgerEvent(String producer, String consumer, String prevEventId) {
        super(producer, consumer, prevEventId, LedgerEventType.KEY_CREATED);
    }

    @NotNull
    private String authority;

    @NotNull
    private String kid;

    @NotNull
    private String jwk;
}
