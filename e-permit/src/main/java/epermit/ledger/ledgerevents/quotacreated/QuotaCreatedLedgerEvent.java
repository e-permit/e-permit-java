package epermit.ledger.ledgerevents.quotacreated;

import epermit.ledger.ledgerevents.LedgerEventBase;
import epermit.ledger.models.enums.PermitType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuotaCreatedLedgerEvent extends LedgerEventBase {

    private int permitYear;

    private PermitType permitType;

    private int startNumber;

    private int endNumber;
}
