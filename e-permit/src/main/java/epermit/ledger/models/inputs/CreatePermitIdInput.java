package epermit.ledger.models.inputs;

import epermit.ledger.models.enums.PermitType;
import lombok.Data;

@Data
public class CreatePermitIdInput {
    private String issuer;
    private String issuedFor;
    private String permitType;
    private String permitYear;
    private String serialNumber;
}