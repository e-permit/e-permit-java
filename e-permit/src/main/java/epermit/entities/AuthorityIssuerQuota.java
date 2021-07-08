package epermit.entities;

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
import epermit.models.enums.PermitType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor // JPA
@Entity
@Table(name = "authority_issuer_ouotas")
public class AuthorityIssuerQuota {
    @Id
    @GeneratedValue
    private int id;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "permit_type", nullable = false)
    private PermitType permitType;

    @Column(name = "permit_year", nullable = false)
    private int permitYear;

    @Column(name = "start_number", nullable = true)
    private Integer startNumber;

    @Column(name = "end_number", nullable = true)
    private Integer endNumber;

    @Column(name = "next_number", nullable = true)
    private Integer nextNumber;

    @Column(name = "used_ledger_quota_ids", nullable = false)
    private String usedLedgerQuotaIds;

    @Column(name = "available_serial_numbers", nullable = false)
    private String availableSerialNumbers;

    @ManyToOne
    @JoinColumn(name = "authority_id") 
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonIgnore
    private Authority authority;
}
