package epermit.models.inputs;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import epermit.models.enums.PermitActivityType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class PermitUsedInput {
    @NotNull
    @Schema(name = "activity_type", description = "Activity type", example = "ENTRANCE" )
    private PermitActivityType activityType;
    @NotNull
    @Schema(name = "activity_timestamp", description = "Activity time", example = "1713529299" )
    private Long activityTimestamp;
    @Size(max = 1000)
    @Schema(name = "activity_details", description = "Activity details", example = "ABC Customs" )
    private String activityDetails;
}
