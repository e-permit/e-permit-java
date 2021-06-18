package epermit.ledgerevents.quotacreated;

import javax.validation.constraints.NotNull;
import epermit.ledgerevents.LedgerEventBase;
import epermit.ledgerevents.LedgerEventType;
import epermit.models.enums.PermitType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuotaCreatedLedgerEvent extends LedgerEventBase {
    public QuotaCreatedLedgerEvent(String issuer, String issuedFor, String prevEventId) {
        super(issuer, issuedFor, prevEventId, LedgerEventType.QUOTA_CREATED);
    }

    @NotNull
    private int permitYear;

    @NotNull
    private PermitType permitType;

    @NotNull
    private int startNumber;

    @NotNull
    private int endNumber;
}
