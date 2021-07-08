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
@Table(name = "ledger_persisted_events")
public class LedgerPersistedEvent {
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
    private LedgerEventType eventType;

    @Column(name = "event_time", nullable = false)
    private Long eventTime;

    @Column(name = "event_content", nullable = false)
    private String eventContent;

    @Column(name = "jws", nullable = false, length = 10000)
    private String jws;
}

