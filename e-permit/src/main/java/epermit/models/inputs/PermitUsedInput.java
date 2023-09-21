package epermit.models.inputs;

import jakarta.validation.constraints.NotNull;
import epermit.models.enums.PermitActivityType;
import lombok.Data;

@Data
public class PermitUsedInput {
    @NotNull
    private PermitActivityType activityType;
    @NotNull
    private Long activityTimestamp;
    private String activityDetails;
}
