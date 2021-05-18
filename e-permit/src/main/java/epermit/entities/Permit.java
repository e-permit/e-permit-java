package epermit.entities;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import epermit.models.enums.PermitType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor // JPA
@Entity
@Table(name = "permits")
@SQLDelete(sql = "UPDATE permits SET deleted = true WHERE id = ?")
@Where(clause = "deleted = false")
public class Permit {
    @Id
    @GeneratedValue
    private Long id;
    
    @Column(name = "issuer", nullable = false)
    private String issuer;

    @Column(name = "permit_id", nullable = false)
    private String permitId;

    @Column(name = "permit_type", nullable = false)
    private PermitType permitType;

    @Column(name = "permit_year", nullable = false)
    private int permitYear;

    @Column(name = "serial_number", nullable = false)
    private int serialNumber;

    @Column(name = "issued_at", nullable = false)
    private String issuedAt;

    @Column(name = "expire_at", nullable = false)
    private String expireAt;

    @Column(name = "plate_number", nullable = false)
    private String plateNumber;

    @Column(name = "company_name", nullable = false)
    private String companyName;

    @Column(name = "claims", nullable = false, length=5000)
    private String claims;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "used", nullable = false)
    private boolean used;

    @Column(name = "revoked", nullable = false)
    private boolean revoked;

    @Column(name = "deleted", nullable = false)
    private boolean deleted;

    @OneToMany(cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private List<PermitActivity> activities = new ArrayList<>();
  
    @JsonIgnore
    public void addActivity(PermitActivity activity) {
        activities.add(activity);
        activity.setPermit(this);
    }
}
