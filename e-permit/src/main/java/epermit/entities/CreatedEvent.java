package epermit.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import com.fasterxml.jackson.annotation.JsonIgnore;
import epermit.ledgerevents.LedgerEventType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

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

    @Column(name = "consumer", nullable = false)
    private String consumer;

    @Column(name = "event_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private LedgerEventType eventType;

    @Column(name = "event_timestamp", nullable = false)
    private Long eventTimestamp;

    @Column(name = "event_content", nullable = false, length = 10000)
    private String eventContent;

    @Column(name = "sended", nullable = false)
    private boolean sended = false;
}
