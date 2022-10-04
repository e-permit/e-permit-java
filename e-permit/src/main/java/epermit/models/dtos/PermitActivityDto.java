package epermit.models.dtos;

import epermit.models.enums.PermitActivityType;
import lombok.Data;

@Data
public class PermitActivityDto{
    private PermitActivityType activityType;
    private String activityTimestamp;
    private String activityDetails;
}