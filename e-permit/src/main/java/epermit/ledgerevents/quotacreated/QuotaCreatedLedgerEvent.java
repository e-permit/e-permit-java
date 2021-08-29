package epermit.ledgerevents.quotacreated;

import javax.validation.constraints.AssertTrue;
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
    public QuotaCreatedLedgerEvent(String producer, String consumer, String prevEventId) {
        super(producer, consumer, prevEventId, LedgerEventType.QUOTA_CREATED);
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

    @AssertTrue(message = "Invalid permit issuer or issued_for")
    private boolean isValid() {
        if (!this.getProducer().equals(this.permitIssuedFor)) {
            return false;
        }
        if (!this.getConsumer().equals(this.permitIssuer)) {
            return false;
        }
        return true;
    }

}
