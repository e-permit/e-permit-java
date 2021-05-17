package epermit.entities;

import java.time.OffsetDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import epermit.models.PermitActivityType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor // JPA
@Entity
@Table(name = "permit_activities")
public class PermitActivity {
    @Id
    @GeneratedValue
    private int id;

    @Column(name = "activity_type", nullable = false)
    private PermitActivityType activityType;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;
    
    @ManyToOne
    @JoinColumn(name = "permit_id") 
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    private Permit permit;

}
