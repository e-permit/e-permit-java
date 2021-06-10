package epermit.ledger.entities;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor // JPA
@Entity
@Table(name = "public_keys")
public class PublicKey {
    @Id
    @GeneratedValue
    private int id;

    @Column(name = "authority_code", nullable = false)
    private String authorityCode;

    @Column(name = "key_id", nullable = false)
    private String keyId;

    @Column(name = "jwk", nullable = false, length = 5000)
    private String jwk;

    @Column(name = "revoked", nullable = false)
    private boolean revoked;

    @Column(name = "revoked_at", nullable = true)
    private Long revokedAt;
    // For authority
    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = true)
    private LocalDateTime updatedAt;
}

