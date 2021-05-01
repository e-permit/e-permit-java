package epermit.entities;

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

@Data
@NoArgsConstructor // JPA
@Entity
@Table(name = "permit_activities")
public class PermitActivity {
    @Id
    @GeneratedValue
    private int id;

    @ManyToOne
    @JoinColumn(name = "permit_id") 
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    private Permit permit;

}
