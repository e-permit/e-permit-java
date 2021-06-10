package epermit.ledger.entities;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import epermit.events.EventType;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "ledger_events")
public class LedgerEvent {
    @Id
    @GeneratedValue
    private long id;

    @Column(name = "issuer", nullable = false)
    private String issuer;

    @Column(name = "issued_for", nullable = false)
    private String issuedFor;
    
    @Column(name = "event_id", nullable = false)
    private String eventId;

    @Column(name = "previous_event_id", nullable = false)
    private String previousEventId;

    @Column(name = "event_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private EventType eventType;

    @Column(name = "event_time", nullable = false)
    private Long eventTime;

    @Column(name = "jws", nullable = false, length=10000)
    private String jws;

    // For authority
    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}

