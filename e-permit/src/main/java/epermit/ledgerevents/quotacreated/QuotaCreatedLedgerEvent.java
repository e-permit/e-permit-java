package epermit.ledgerevents.quotacreated;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import epermit.ledgerevents.LedgerEventBase;
import epermit.ledgerevents.LedgerEventType;
import epermit.models.enums.PermitType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuotaCreatedLedgerEvent extends LedgerEventBase {
    public QuotaCreatedLedgerEvent(String eventIssuer, String eventIssuedFor, String prevEventId) {
        super(eventIssuer, eventIssuedFor, prevEventId, LedgerEventType.QUOTA_CREATED);
    }

    @NotNull
    @Size(min = 2, max = 2)
    private String permitIssuer;

    @NotNull
    @Size(min = 2, max = 2)
    private String permitIssuedFor;

    @NotNull
    @Min(2021)
    private int permitYear;

    @NotNull
    private PermitType permitType;

    @NotNull
    @Min(1)
    private int startNumber;

    @NotNull
    @Min(1)
    private int endNumber;

}
