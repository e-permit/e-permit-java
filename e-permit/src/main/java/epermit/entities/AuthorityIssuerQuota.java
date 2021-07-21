package epermit.entities;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import com.fasterxml.jackson.annotation.JsonIgnore;
import epermit.commons.IntegerListConverter;
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

    @Column(name = "used_ledger_quota_ids", nullable = true)
    @Convert(converter = IntegerListConverter.class)
    private List<Integer> usedLedgerQuotaIds = new ArrayList<>();

    public void addLedgerQuotaId(Integer id){
        this.usedLedgerQuotaIds.add(id);
    }

    @Column(name = "revoked_serial_numbers", nullable = true)
    @Convert(converter = IntegerListConverter.class)
    private List<Integer> revokedSerialNumbers = new ArrayList<>();

    public void addRevokedSerialNumber(Integer sn){
        this.revokedSerialNumbers.add(sn);
    }

    @ManyToOne
    @JoinColumn(name = "authority_id") 
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonIgnore
    private Authority authority;
}
