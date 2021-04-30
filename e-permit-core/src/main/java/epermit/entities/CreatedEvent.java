package epermit.entities;

import java.time.OffsetDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import epermit.common.EventState;
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

    @Column(name = "issued_for", nullable = false)
    private String issuedFor;

    @Column(name = "event_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private EventType eventType;

    @Column(name = "content", nullable = false, length=10000)
    private String content;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "state", nullable = false)
    private EventState state;

    @Column(name = "succeed", nullable = false)
    private Boolean succeed;

    @Column(name = "error_code", nullable = true)
    private Boolean errorCode;

    @Column(name = "locked_at", nullable = false)
    private OffsetDateTime lockedAt;

    @Column(name = "handled_at", nullable = false)
    private OffsetDateTime sendedAt;
}