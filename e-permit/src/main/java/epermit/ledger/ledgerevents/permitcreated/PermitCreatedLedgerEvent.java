package epermit.ledger.ledgerevents.permitcreated;

import java.util.Map;
import epermit.ledger.ledgerevents.LedgerEventBase;
import epermit.ledger.models.enums.PermitType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PermitCreatedLedgerEvent extends LedgerEventBase {
    private String permitId;
    private PermitType permitType;
    private int permitYear;
    private int serialNumber;
    private String IssuedAt;
    private String ExpireAt;
    private String companyName;
    private String companyId;
    private String plateNumber;
    private Map<String, Object> claims;
}