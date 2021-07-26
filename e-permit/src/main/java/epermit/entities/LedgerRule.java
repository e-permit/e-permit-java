package epermit.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "ledger_rules")
public class LedgerRule {
    @Id
    @GeneratedValue
    private int id;

    @Column(name = "issuer", nullable = false)
    private String issuer;

    @Column(name = "issued_for", nullable = false)
    private String issuedFor;

    @Column(name = "rule", nullable = false)
    public String rule;
}
