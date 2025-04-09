package epermit.models.dtos;

import lombok.Data;

@Data
public class HealthCheckResultItem {
    private String authority;
    
    private boolean ok;

    private String problem;
}
