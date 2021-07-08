package epermit.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vladmihalcea.hibernate.type.json.JsonType;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.Where;
import epermit.models.enums.PermitType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor // JPA
@Entity
@Table(name = "ledger_permits")
@SQLDelete(sql = "UPDATE ledger_permits SET deleted = true WHERE id = ?")
@Where(clause = "deleted = false")
public class LedgerPermit {
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "permit_id", nullable = false)
    private String permitId;

    @Column(name = "issuer", nullable = false)
    private String issuer;

    @Column(name = "issued_for", nullable = false)
    private String issuedFor;

    @Column(name = "serial_number", nullable = false)
    private int serialNumber;

    @Column(name = "permit_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private PermitType permitType;

    @Column(name = "permit_year", nullable = false)
    private int permitYear;

    @Column(name = "issued_at", nullable = false)
    private String issuedAt;

    @Column(name = "expire_at", nullable = false)
    private String expireAt;

    @Column(name = "plate_number", nullable = false)
    private String plateNumber;

    @Column(name = "company_name", nullable = false, length = 200)
    private String companyName;

    @Column(name = "company_id", nullable = false, length = 100)
    private String companyId;

    @Type(type = "json")
    private String claims;

    @Column(name = "qr_code", nullable = false, length=1000)
    private String qrCode;

    @Column(name = "used", nullable = false)
    private boolean used;

    @Column(name = "deleted", nullable = false)
    private boolean deleted;

    
    @OneToMany(cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private List<LedgerPermitActivity> activities = new ArrayList<>();
  
    @JsonIgnore
    public void addActivity(LedgerPermitActivity activity) {
        activities.add(activity);
        activity.setLedgerPermit(this);
    }
}

