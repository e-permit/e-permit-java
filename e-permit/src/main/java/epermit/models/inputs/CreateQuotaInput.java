package epermit.models.inputs;

import jakarta.validation.constraints.NotNull;
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
    private Long quantity;
}
