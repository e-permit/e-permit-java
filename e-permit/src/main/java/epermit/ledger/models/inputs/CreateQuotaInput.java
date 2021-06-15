package epermit.ledger.models.inputs;

import epermit.ledger.models.enums.PermitType;
import lombok.Data;

@Data
public class CreateQuotaInput {
    private String authorityCode;

    private int permitYear;

    private PermitType permitType;

    private int startId;

    private int endId;
}
