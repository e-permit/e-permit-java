package epermit.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import epermit.models.enums.PermitType;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "ledger_quotas")
public class LedgerQuota {
    @Id
    @GeneratedValue
    private int id;

    @Column(name = "permit_issuer", nullable = false)
    private String permitIssuer;

    @Column(name = "permit_issued_for", nullable = false)
    private String permitIssuedFor;

    @Column(name = "permit_year", nullable = false)
    private int permitYear;

    @Column(name = "permit_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private PermitType permitType;

    @Column(name = "start_number", nullable = false)
    private int startNumber;

    @Column(name = "end_number", nullable = false)
    private int endNumber;

    @Column(name = "active", nullable = false)
    private boolean active;
}


