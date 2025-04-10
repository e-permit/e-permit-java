package epermit.models.inputs;

import java.util.HashMap;
import java.util.Map;

import epermit.commons.Constants;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(
    description = "Create permit input",
    example = """
        {
            "issued_for": "A",
            "permit_type": 1,
            "permit_year": 2025,
            "plate_number": "TEST",
            "company_name": "ABC",
            "company_id": "123",
            "departure_country": "C",
            "arrival_country": "D"
        }
        """
)
public class CreatePermitInput {

    @NotNull
    @Size(max = 255)
    @Schema(name = "issued_for", description = "Permit issued for")
    private String issuedFor;

    @NotNull
    @Schema(name = "permit_type", description = "Permit type")
    private Integer permitType;

    @NotNull
    @Schema(name = "permit_year", description = "Permit year")
    private int permitYear;

    @Size(max = 255)
    @Schema(name = "plate_number", description = "Plate number")
    private String plateNumber;

    @Size(max = 255)
    @Schema(name = "plate_number2", description = "Plate number 2")
    private String plateNumber2;

    @NotNull
    @Size(max = 500)
    @Schema(name = "company_name", description = "Company name")
    private String companyName;

    @NotNull
    @Size(max = 255)
    @Schema(name = "company_id", description = "Company identifier")
    private String companyId;

    @Size(max = 10)
    @Schema(name = "departure_country", description = "Departure country")
    private String departureCountry;

    @NotNull
    @Size(max = 10)
    @Schema(name = "arrival_country", description = "Arrival country")
    private String arrivalCountry;

    @Pattern(regexp = Constants.DATE_FORMAT)
    @Schema(name = "expires_at", description = "Expires at", pattern = "dd/MM/yyyy", example = "01/01/2025")
    private String expiresAt;

    @Schema(description = "Other custom fields", example ="""
            {
               "custom_field": "value"
            }
            """)
    private Map<String, Object> otherClaims = new HashMap<>();
}
