package epermit.entities;

import java.time.LocalDateTime;
import java.util.UUID;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import epermit.ledgerevents.LedgerEventType;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "epermit_ledger_events")
public class LedgerEvent {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
        name = "UUID",
        strategy = "org.hibernate.id.UUIDGenerator"
    )
    private UUID id;

    @Column(name = "producer", nullable = false)
    private String producer;

    @Column(name = "consumer", nullable = false)
    private String consumer;

    @Column(name = "event_id", nullable = false)
    private String eventId;

    @Column(name = "previous_event_id", nullable = false)
    private String previousEventId;

    @Column(name = "event_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private LedgerEventType eventType;

    @Column(name = "event_timestamp", nullable = false)
    private Long eventTimestamp;

    @Column(name = "event_content", nullable = false, length = 10000)
    private String eventContent;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}

