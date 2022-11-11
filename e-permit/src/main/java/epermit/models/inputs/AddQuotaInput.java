package epermit.models.inputs;

import javax.validation.constraints.NotNull;
import epermit.models.enums.PermitType;
import lombok.Data;

@Data
public class AddQuotaInput {
    @NotNull
    private String authorityCode;
    @NotNull
    private int permitYear;
    @NotNull
    private PermitType permitType;
    @NotNull
    private int quantity;
}
