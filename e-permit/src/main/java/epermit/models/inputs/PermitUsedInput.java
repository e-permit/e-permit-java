package epermit.models.inputs;

import epermit.models.enums.PermitActivityType;
import lombok.Data;

@Data
public class PermitUsedInput {
    private String permitId;
    private PermitActivityType activityType;
}
