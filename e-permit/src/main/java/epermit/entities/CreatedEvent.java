package epermit.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
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

    @Column(name = "sended", nullable = false)
    private boolean sended = false;

    @Column(name = "result", nullable = true)
    private String result;
}
