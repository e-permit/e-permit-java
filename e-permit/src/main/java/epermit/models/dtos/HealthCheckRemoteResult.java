package epermit.models.dtos;

import lombok.Data;

@Data
public class HealthCheckRemoteResult {
    private String toLastEventId;
    private String fromLastEventId;
}
