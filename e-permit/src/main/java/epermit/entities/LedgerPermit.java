package epermit.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
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
@Table(name = "epermit_ledger_permits")
@SQLDelete(sql = "UPDATE epermit_ledger_permits SET deleted = '1' WHERE id = ?")
@Where(clause = "deleted = false")
public class LedgerPermit {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
        name = "UUID",
        strategy = "org.hibernate.id.UUIDGenerator"
    )
    private UUID id;

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

    @Column(name = "qr_code", nullable = false, length = 5000)
    private String qrCode;

    @Column(name = "other_claims", nullable = true)
    private String otherClaims;

    @Column(name = "used", nullable = false)
    private boolean used;

    @Column(name = "locked", nullable = false)
    private boolean locked;

    @Column(name = "deleted", nullable = false)
    private boolean deleted;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "ledgerPermit")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private List<LedgerPermitActivity> activities = new ArrayList<>();
  
    @JsonIgnore
    public void addActivity(LedgerPermitActivity activity) {
        activities.add(activity);
        activity.setLedgerPermit(this);
    }
}

