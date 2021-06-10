package epermit.ledger.entities;


import java.time.LocalDateTime;

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
import org.hibernate.annotations.CreationTimestamp;
import epermit.models.enums.PermitActivityType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor // JPA
@Entity
@Table(name = "permit_activities")
public class PermitActivity {
    @Id
    @GeneratedValue
    private int id;

    @Column(name = "activity_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private PermitActivityType activityType;

    @Column(name = "activity_timestamp", nullable = false)
    private Long activityTimestamp;

    @ManyToOne
    @JoinColumn(name = "permit_id") 
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonIgnore
    private Permit permit;

    // For authority
    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

}
