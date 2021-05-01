package epermit.entities;

import java.time.OffsetDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import epermit.common.EventType;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "created_events")
public class CreatedEvent {
    @Id
    @GeneratedValue
    private long id;
    
    @Column(name = "event_id", nullable = false)
    private String eventId;

    @Column(name = "previous_event_id", nullable = false)
    private String previousEventId;

    @Column(name = "event_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private EventType eventType;

    @Column(name = "issued_for", nullable = false)
    private String issuedFor;

    @Column(name = "jws", nullable = false, length=10000)
    private String jws;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;
}