package epermit.models.dtos;

import epermit.ledgerevents.LedgerEventType;

import lombok.Data;

@Data
public class EventDto {

    private String eventId;

    private String previousEventId;

    private LedgerEventType eventType;

    private Long eventTimestamp;

    private String eventContent;
    
    private boolean sent;
}
