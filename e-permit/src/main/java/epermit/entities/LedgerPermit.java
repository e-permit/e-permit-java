package epermit.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor 
@Entity
@Table(name = "epermit_ledger_permits")
public class LedgerPermit {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
        name = "UUID",
        strategy = "org.hibernate.id.UUIDGenerator"
    )
    private UUID id;

    @Column(name = "permit_id", nullable = false, unique = true)
    private String permitId;

    @Column(name = "issuer", nullable = false)
    private String issuer;

    @Column(name = "issued_for", nullable = false)
    private String issuedFor;

    @Column(name = "permit_type", nullable = false)
    private Integer permitType;

    @Column(name = "permit_year", nullable = false)
    private int permitYear;

    @Column(name = "issued_at", nullable = false)
    private String issuedAt;

    @Column(name = "expires_at", nullable = false)
    private String expiresAt;

    @Column(name = "plate_number", nullable = true)
    private String plateNumber;

    @Column(name = "plate_number2", nullable = true)
    private String plateNumber2;

    @Column(name = "company_name", nullable = false, length = 500)
    private String companyName;

    @Column(name = "company_id", nullable = false)
    private String companyId;

    @Column(name = "departure_country", nullable = false, length = 10)
    private String departureCountry;

    @Column(name = "arrival_country", nullable = false, length = 10)
    private String arrivalCountry;

    @Column(name = "other_claims", nullable = true)
    private String otherClaims;

    @Column(name = "qr_code", nullable = false, length = 5000)
    private String qrCode;

    @Column(name = "used", nullable = false)
    private boolean used;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "revoked", nullable = false)
    private boolean revoked;

    @Column(name = "revoked_at", nullable = true)
    private Long revokedAt;
    
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

