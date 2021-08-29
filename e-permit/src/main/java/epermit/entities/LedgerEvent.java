package epermit.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import epermit.ledgerevents.LedgerEventType;
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

    @Column(name = "proof", nullable = false, length = 1000)
    private String proof;
}

