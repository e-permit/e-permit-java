package epermit.ledgerevents.permitrevoked;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import epermit.ledgerevents.LedgerEventBase;
import epermit.ledgerevents.LedgerEventType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PermitRevokedLedgerEvent extends LedgerEventBase {
    public PermitRevokedLedgerEvent(String producer, String consumer, String prevEventId) {
        super(producer, consumer, prevEventId, LedgerEventType.PERMIT_REVOKED);
    }

    @NotNull
    @Pattern(regexp = "^[A-Z]{2}-[A-Z]{2}-\\d{4}-(1|2|3)-[0-9]+$")
    private String permitId;
}
