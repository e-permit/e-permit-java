package epermit.models.dtos;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
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
