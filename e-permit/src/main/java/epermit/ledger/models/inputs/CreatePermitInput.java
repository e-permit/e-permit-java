package epermit.ledger.models.inputs;

import java.util.HashMap;
import java.util.Map;
import epermit.ledger.models.enums.PermitType;
import lombok.Data;

@Data
public class CreatePermitInput {
    private String issuedFor;

    private PermitType permitType;

    private int permitYear;

    private String plateNumber;

    private String companyName;

    private String companyId;

    private Map<String, Object> claims = new HashMap<>();
}