package epermit.ledger.entities;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor // JPA
@Entity
@Table(name = "keys")
@SQLDelete(sql = "UPDATE keys SET deleted = true WHERE id = ?")
@Where(clause = "deleted = false")
public class PrivateKey {
    @Id
    @GeneratedValue
    private int id;

    @Column(name = "key_id", nullable = false)
    private String keyId;

    @Column(name = "salt", nullable = false)
    private String salt;

    @Column(name = "private_jwk", nullable = false, length=4000)
    private String privateJwk;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
  
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "enabled", nullable = false)
    private boolean enabled;

    @Column(name = "deleted", nullable = false)
    private boolean deleted;
}

