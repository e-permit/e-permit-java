package epermit.events;

import epermit.common.EventType;
import lombok.Data;

@Data
public class EventBase {
    private String issuer;

    private String issuedFor;

    private Long createdAt;

    private EventType eventType;

    private String eventId;
}
