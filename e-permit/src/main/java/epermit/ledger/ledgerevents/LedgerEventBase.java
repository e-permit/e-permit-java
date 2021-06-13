package epermit.ledger.ledgerevents;


import lombok.Data;

@Data
public class LedgerEventBase {
    private String issuer;

    private String issuedFor;

    private Long eventTimestamp;

    private LedgerEventType eventType;

    private String eventId;

    private String previousEventId;
}
