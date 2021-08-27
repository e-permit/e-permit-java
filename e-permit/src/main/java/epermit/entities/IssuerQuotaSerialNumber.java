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
import epermit.models.enums.IssuerQuotaSerialNumberState;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor // JPA
@Entity
@Table(name = "issuer_quota_serial_numbers")
public class IssuerQuotaSerialNumber {
    @Id
    @GeneratedValue
    private int id;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false)
    private IssuerQuotaSerialNumberState state;

    @Column(name = "serial_number", nullable = false)
    private int serialNumber;

    @ManyToOne
    @JoinColumn(name = "ledger_quota_id") 
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonIgnore
    private LedgerQuota ledgerQuota;
}
