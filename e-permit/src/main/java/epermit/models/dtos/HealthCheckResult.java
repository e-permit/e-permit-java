package epermit.models.dtos;

import java.util.List;

import lombok.Data;

@Data
public class HealthCheckResult {
    private boolean ok;

    private List<HealthCheckResultItem> authorities;
}
