package epermit.entities;

import java.time.OffsetDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor // JPA
@Entity
@Table(name = "keys")
public class Key {
    @Id
    @GeneratedValue
    private int id;

    @Column(name = "key_id", nullable = false)
    private String keyId;
    
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "valid_from", nullable = true)
    private Long validFrom;

    @Column(name = "valid_until", nullable = true)
    private Long validUntil;

    @Column(name = "public_jwk", nullable = false, length=4000)
    private String publicJwk;

    @Column(name = "salt", nullable = false)
    private String salt;

    @Column(name = "private_jwk", nullable = false, length=4000)
    private String privateJwk;

    @Column(name = "active", nullable = false)
    private boolean active;
}
