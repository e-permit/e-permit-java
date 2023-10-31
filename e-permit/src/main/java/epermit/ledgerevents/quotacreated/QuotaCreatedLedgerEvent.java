package epermit.ledgerevents.quotacreated;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
    private Long quantity;

    @AssertTrue(message = "Invalid permit issuer or issued_for")
    private boolean isValid() {
        if (!this.getEventProducer().equals(this.permitIssuedFor)) {
            return false;
        }
        if (!this.getEventConsumer().equals(this.permitIssuer)) {
            return false;
        }
        return true;
    }

}
