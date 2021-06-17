package epermit.ledgerevents;

import java.time.Instant;
import java.util.UUID;
import lombok.Getter;

@Getter
public class LedgerEventBase {
    private String issuer;

    private String issuedFor;

    private Long eventTimestamp;

    private LedgerEventType eventType;

    private String eventId;

    private String previousEventId;

    public LedgerEventBase(String issuer, String issuedFor, String preEventId, LedgerEventType eventType) {
        this.eventId = UUID.randomUUID().toString();
        this.eventTimestamp = Instant.now().getEpochSecond();
        this.eventType = eventType;
        this.issuedFor = issuedFor;
        this.issuer = issuer;
        this.previousEventId = preEventId;
    }
}
