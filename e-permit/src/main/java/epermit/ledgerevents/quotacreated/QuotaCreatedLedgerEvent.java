package epermit.ledgerevents.quotacreated;

import epermit.ledgerevents.LedgerEventBase;
import epermit.models.enums.PermitType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuotaCreatedLedgerEvent extends LedgerEventBase {

    private int permitYear;

    private PermitType permitType;

    private int startNumber;

    private int endNumber;

    public QuotaCreatedLedgerEvent() {
        super();
    }
}
