package epermit.models.inputs;

import java.util.HashMap;
import java.util.Map;
import jakarta.validation.constraints.NotNull;
import epermit.models.enums.PermitType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class CreatePermitInput {

    @NotNull
    @Schema(name = "issued_for", description = "Permit issued for", example = "TR" )
    private String issuedFor;

    @NotNull
    @Schema(description = "Permit type", example = "BILATERAL" )
    private PermitType permitType;
    
    @NotNull
    @Schema(description = "Permit year", example = "2024" )
    private int permitYear;
    
    @NotNull
    @Schema(description = "Plate number", example = "06TEST" )
    private String plateNumber;
    
    @NotNull
    @Schema(description = "Company name", example = "ABC" )
    private String companyName;
    
    @NotNull
    @Schema(description = "Company identifier", example = "1234" )
    private String companyId;
    
    @Schema(description = "Arrival country", example = "TR" )
    private String arrivalCountry;
    
    @Schema(description = "Other data", example = "{}" )
    private Map<String, Object> otherClaims = new HashMap<>();
}