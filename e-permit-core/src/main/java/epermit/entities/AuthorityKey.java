package epermit.entities;

import java.time.OffsetDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor // JPA
@Entity
@Table(name = "authority_keys")
public class AuthorityKey {
    @Id
    @GeneratedValue
    private int id;

    @Column(name = "kid", nullable = false)
    private String kid;
    
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "disabled", nullable = true)
    private Boolean disabled;

    @Column(name = "disabled_at", nullable = true)
    private OffsetDateTime disabledAt;

    @Column(name = "jwk", nullable = false, length=5000)
    private String jwk;
    
    @ManyToOne
    @JoinColumn(name = "authority_id")
    @EqualsAndHashCode.Exclude
    @JsonIgnore // Avoid infinite loops
    private Authority authority;
}
