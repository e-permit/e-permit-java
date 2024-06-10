package epermit.models.inputs;

import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class CreateQuotaInput {
    @NotNull
    @Schema(name = "permit_year",description = "Quota year", example = "2024" )
    private int permitYear;
    @NotNull
    @Schema(name = "permit_type", description = "Permit type", example = "1" )
    private Integer permitType;
    @NotNull
    @Schema(description = "Quota quantity", example = "100" )
    private Long quantity;
}
