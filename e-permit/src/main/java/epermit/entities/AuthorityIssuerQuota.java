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
import org.hibernate.annotations.Type;
import epermit.models.enums.PermitType;
import epermit.models.valueobjects.AuthorityIssuerQuotaPayload;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor // JPA
@Entity
@Table(name = "authority_issuer_quota")
public class AuthorityIssuerQuota {

    @Id
    @GeneratedValue
    private int id;
    
    @Column(name = "permit_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private PermitType permitType;

    @Column(name = "permit_year", nullable = false)
    private int permitYear;

    @Type(type = "json")
    @Column(name = "payload", columnDefinition = "jsonb")
    private AuthorityIssuerQuotaPayload payload;

    @ManyToOne
    @JoinColumn(name = "authority_id")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonIgnore
    private Authority authority;
}

