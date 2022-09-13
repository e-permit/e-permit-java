package epermit.models.inputs;

import javax.validation.constraints.NotNull;
import epermit.models.enums.PermitType;
import lombok.Data;

@Data
public class CreateQuotaInput {
    @NotNull
    private String authorityCode;
    @NotNull
    private int permitYear;
    @NotNull
    private PermitType permitType;
    @NotNull
    private int startNumber;
    @NotNull
    private int endNumber;
}
