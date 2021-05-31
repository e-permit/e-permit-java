package epermit.entities;

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
import org.hibernate.annotations.UpdateTimestamp;
import epermit.models.enums.PermitActivityType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor // JPA
@Entity
@Table(name = "issued_permit_activities")
public class IssuedPermitActivity {
    @Id
    @GeneratedValue
    private int id;

    @Column(name = "activity_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private PermitActivityType activityType;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
  
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "issued_permit_id") 
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonIgnore
    private IssuedPermit issuedPermit;

}
