package epermit.entities;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@Entity
@Table(name = "created_events")
public class AuthorityEvent {
    @Id
    @GeneratedValue
    private long id;

    @Column(name = "event_id", nullable = false)
    private String eventId;

    @Column(name = "sended", nullable = false)
    private boolean sended = false;

    @Column(name = "result", nullable = true)
    private String result;

    @ManyToOne
    @JoinColumn(name = "authority_id") 
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonIgnore
    private Authority authority;
}
