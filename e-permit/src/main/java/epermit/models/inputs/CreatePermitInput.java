package epermit.models.inputs;

import java.util.HashMap;
import java.util.Map;

import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class CreatePermitInput {

    @NotNull
    @Schema(name = "issued_for", description = "Permit issued for", example = "TR" )
    private String issuedFor;

    @NotNull
    @Schema(name = "permit_type", description = "Permit type", example = "1" )
    private Integer permitType;
    
    @NotNull
    @Schema(name = "permit_year", description = "Permit year", example = "2024" )
    private int permitYear;
    
    @NotNull
    @Schema(name = "plate_number", description = "Plate number", example = "06TEST" )
    private String plateNumber;
    
    @NotNull
    @Schema(name = "company_name", description = "Company name", example = "ABC" )
    private String companyName;
    
    @NotNull
    @Schema(name = "company_id", description = "Company identifier", example = "1234" )
    private String companyId;
    
    @NotNull
    @Schema(name = "arrival_country", description = "Arrival country", example = "TR" )
    private String arrivalCountry;
        
    @Schema(hidden = true, name = "ex_permit_id", description = "Ex permit id")
    private String prevPermitId;

    @Schema(hidden = true, name = "expires_at", description = "Expires at")
    private String expireAt;
    
    @Schema(hidden = true )
    private Map<String, Object> otherClaims = new HashMap<>();
}