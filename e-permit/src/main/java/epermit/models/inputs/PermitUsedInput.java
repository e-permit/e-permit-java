package epermit.models.inputs;

import jakarta.validation.constraints.NotNull;
import epermit.models.enums.PermitActivityType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class PermitUsedInput {
    @NotNull
    @Schema(description = "Activity type", example = "ENTRANCE" )
    private PermitActivityType activityType;
    @NotNull
    @Schema(description = "Activity time", example = "1713529299" )
    private Long activityTimestamp;
    @Schema(description = "Activity details", example = "ABC Customs" )
    private String activityDetails;
}
