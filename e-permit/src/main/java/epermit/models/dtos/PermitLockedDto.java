package epermit.models.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PermitLockedDto {
    @NotNull
    @Size(min = 2, max = 2)
    private String eventProducer;

    @NotNull
    @Size(min = 2, max = 2)
    private String eventConsumer;

    @NotNull
    @Min(1609459200)
    private Long eventTimestamp;
}
