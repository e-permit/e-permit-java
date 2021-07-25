package epermit.models.inputs;

import epermit.models.enums.PermitType;
import lombok.Data;

@Data
public class CreateQuotaInput {
    private String authorityCode;

    private int permitYear;

    private PermitType permitType;

    private int startNumber;

    private int endNumber;
}
