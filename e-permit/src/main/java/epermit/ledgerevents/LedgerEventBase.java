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

    public static LedgerEventBase create(String issuer, String issuedFor, String preEventId, LedgerEventType eventType) {
        LedgerEventBase eventBase = new LedgerEventBase();
        eventBase.eventId = UUID.randomUUID().toString();
        eventBase.eventTimestamp = Instant.now().getEpochSecond();
        eventBase.eventType = eventType;
        eventBase.issuedFor = issuedFor;
        eventBase.issuer = issuer;
        eventBase.previousEventId = preEventId;
        return eventBase;
    }
}
