package epermit.ledgerevents.permitused;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
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
    @Pattern(regexp = "^[A-Z]{2}-[A-Z]{2}-\\d{4}-(1|2|3)-[0-9]+$")
    private String permitId;

    @NotNull
    private PermitActivityType activityType;

    @NotNull
    @Min(1609459200)
    private Long activityTimestamp;

    private String activityDetails;
}
