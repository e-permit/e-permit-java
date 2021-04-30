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
@Table(name = "permits")
@SQLDelete(sql = "UPDATE permits SET deleted = true WHERE id = ?")
@Where(clause = "deleted = false")
public class Permit {
    @Id
    @GeneratedValue
    private Long id;
    
    @Column(name = "issuer", nullable = false)
    private String issuer;

    @Column(name = "serial_number", nullable = false)
    private String serialNumber;

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
}
