package epermit.models.inputs;

import java.util.HashMap;
import java.util.Map;

import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class CreatePermitInput {

    @NotNull
    @Schema(name = "issued_for", description = "Permit issued for", example = "A" )
    private String issuedFor;

    @NotNull
    @Schema(name = "permit_type", description = "Permit type", example = "1" )
    private Integer permitType;
    
    @NotNull
    @Schema(name = "permit_year", description = "Permit year", example = "2024" )
    private int permitYear;
    
    @Schema(name = "plate_number", description = "Plate number", example = "06TEST" )
    private String plateNumber;

    @Schema(name = "plate_number2", description = "Plate number 2", example = "06TEST" )
    private String plateNumber2;
    
    @NotNull
    @Schema(name = "company_name", description = "Company name", example = "ABC" )
    private String companyName;
    
    @NotNull
    @Schema(name = "company_id", description = "Company identifier", example = "1234" )
    private String companyId;
    
    @NotNull
    @Schema(name = "arrival_country", description = "Arrival country", example = "A" )
    private String arrivalCountry;

    @Schema(name = "expire_at", description = "Expires at (nullable), default: last day of upcoming year's January", example= "02/01/2023")
    private String expireAt;
    
    @Schema(hidden = true )
    private Map<String, Object> otherClaims = new HashMap<>();
}