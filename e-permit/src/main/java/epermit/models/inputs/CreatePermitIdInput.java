package epermit.models.inputs;

import epermit.models.enums.PermitType;
import lombok.Data;

@Data
public class CreatePermitIdInput {
    private String issuer;
    private String issuedFor;
    private PermitType permitType;
    private int permitYear;
    private int serialNumber;
}
