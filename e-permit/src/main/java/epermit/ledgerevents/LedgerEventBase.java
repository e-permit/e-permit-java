package epermit.ledgerevents;

import java.time.Instant;
import java.util.UUID;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Getter;

@Getter
public class LedgerEventBase {
    @NotNull
    @Size(min = 2, max = 2)
    private String producer;

    @NotNull
    @Size(min = 2, max = 2)
    private String consumer;

    @NotNull
    @Min(1609459200)
    private Long eventTimestamp;

    @NotNull
    private LedgerEventType eventType;

    @NotNull
    @Size(min = 16, max = 100)
    private String eventId;

    @NotNull
    @Size(min = 1, max = 100)
    private String previousEventId;

    public LedgerEventBase(String producer, String consumer, String prevEventId, LedgerEventType eventType) {
        this.eventId = UUID.randomUUID().toString();
        this.eventTimestamp = Instant.now().getEpochSecond();
        this.eventType = eventType;
        this.producer = producer;
        this.consumer = consumer;
        this.previousEventId = prevEventId;
    }
}
