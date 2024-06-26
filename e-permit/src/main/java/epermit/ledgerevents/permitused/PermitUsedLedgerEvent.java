package epermit.ledgerevents.permitused;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import epermit.commons.Constants;
import epermit.ledgerevents.LedgerEventBase;
import epermit.ledgerevents.LedgerEventType;
import epermit.models.enums.PermitActivityType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PermitUsedLedgerEvent extends LedgerEventBase {

    public PermitUsedLedgerEvent(String producer, String consumer, String prevEventId) {
        super(producer, consumer, prevEventId, LedgerEventType.PERMIT_USED);
    }

    @NotNull
    @Pattern(regexp = Constants.PERMIT_ID_FORMAT)
    private String permitId;

    @NotNull
    private PermitActivityType activityType;

    @NotNull
    @Min(1609459200)
    private Long activityTimestamp;

    private String activityDetails;
}
