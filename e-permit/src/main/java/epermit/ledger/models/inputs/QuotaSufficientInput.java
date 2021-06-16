package epermit.ledger.models.inputs;

import epermit.ledger.models.enums.PermitType;
import lombok.Data;

@Data
public class QuotaSufficientInput {
    private String issuer;
    private String issuedFor;
    private PermitType permitType;
    private int permitYear;
    private int serialNumber;
}
