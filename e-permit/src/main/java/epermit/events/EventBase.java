package epermit.events;

import lombok.Data;

@Data
public class EventBase {
    private String issuer;

    private String issuedFor;

    private Long createdAt;

    private EventType eventType;

    private String eventId;

    private String previousEventId;
}
