package epermit.models.inputs;

import java.util.HashMap;
import java.util.Map;
import javax.validation.constraints.NotNull;
import epermit.models.enums.PermitType;
import lombok.Data;

@Data
public class CreatePermitInput {
    @NotNull
    private String issuedFor;
    @NotNull
    private PermitType permitType;
    @NotNull
    private int permitYear;
    @NotNull
    private String plateNumber;
    @NotNull
    private String companyName;
    @NotNull
    private String companyId;
    private Map<String, Object> otherClaims = new HashMap<>();
}