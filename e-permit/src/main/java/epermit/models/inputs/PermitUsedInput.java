package epermit.models.inputs;

import epermit.models.enums.PermitActivityType;
import lombok.Data;

@Data
public class PermitUsedInput {
    private PermitActivityType activityType;
}
