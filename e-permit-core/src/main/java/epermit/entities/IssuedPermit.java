package epermit.entities;

import java.time.OffsetDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import epermit.common.PermitType;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor // JPA
@Entity
@Table(name = "issued_permits")
@SQLDelete(sql = "UPDATE issued_permits SET deleted = true WHERE id = ?")
@Where(clause = "deleted = false")
public class IssuedPermit {
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "serial_number", nullable = false)
    private String serialNumber;

    @Column(name = "qr_code", nullable = false, length=1000)
    private String qrCode;

    @Column(name = "issued_for", nullable = false)
    private String issuedFor;

    @Column(name = "permit_id", nullable = false)
    private int permitId;

    @Column(name = "permit_type", nullable = false)
    private PermitType permitType;

    @Column(name = "permit_year", nullable = false)
    private int permitYear;

    @Column(name = "issued_at", nullable = false)
    private String issuedAt;

    @Column(name = "expire_at", nullable = false)
    private String expireAt;

    @Column(name = "plate_number", nullable = false)
    private String plateNumber;

    @Column(name = "company_name", nullable = false)
    private String companyName;

    @Column(name = "claims", nullable = false, length=1000)
    private String claims;

    @Column(name = "used", nullable = false)
    private boolean used;

    @Column(name = "used_at", nullable = true)
    private OffsetDateTime usedAt;

    @Column(name = "revoked", nullable = false)
    private boolean revoked;

    @Column(name = "revoked_at", nullable = true)
    private OffsetDateTime revokedAt;

    @Column(name = "deleted", nullable = false)
    private boolean deleted;
}
