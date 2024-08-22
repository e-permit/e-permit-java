package epermit.models.inputs;

import java.util.HashMap;
import java.util.Map;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class CreatePermitInput {

    @NotNull
    @Size(max = 255)
    @Schema(name = "issued_for", description = "Permit issued for", example = "A")
    private String issuedFor;

    @NotNull
    @Schema(name = "permit_type", description = "Permit type", example = "1")
    private Integer permitType;

    @NotNull
    @Schema(name = "permit_year", description = "Permit year", example = "2024")
    private int permitYear;

    @Size(max = 255)
    @Schema(name = "plate_number", description = "Plate number", example = "06TEST")
    private String plateNumber;

    @Size(max = 255)
    @Schema(name = "plate_number2", description = "Plate number 2", example = "06TEST")
    private String plateNumber2;

    @NotNull
    @Size(max = 500)
    @Schema(name = "company_name", description = "Company name", example = "ABC")
    private String companyName;

    @NotNull
    @Size(max = 255)
    @Schema(name = "company_id", description = "Company identifier", example = "1234")
    private String companyId;

    @NotNull
    @Size(max = 10)
    @Schema(name = "arrival_country", description = "Arrival country", example = "A")
    private String arrivalCountry;

    @Schema(hidden = true, name = "expires_at", description = "Expires at")
    private String expiresAt;

    @Schema(hidden = true)
    private Map<String, Object> otherClaims = new HashMap<>();
}
