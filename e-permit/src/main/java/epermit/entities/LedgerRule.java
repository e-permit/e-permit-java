package epermit.entities;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

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
