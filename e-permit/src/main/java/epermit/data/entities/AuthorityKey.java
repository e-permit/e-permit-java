package epermit.data.entities;

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

    @Column(name = "key_id", nullable = false)
    private String keyId;

    @Column(name = "jwk", nullable = false, length = 5000)
    private String jwk;

    @Column(name = "valid_from", nullable = false)
    private Long validFrom;

    @Column(name = "valid_until", nullable = true)
    private Long validUntil;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "authority_id")
    @EqualsAndHashCode.Exclude
    @JsonIgnore // Avoid infinite loops
    private Authority authority;
}
