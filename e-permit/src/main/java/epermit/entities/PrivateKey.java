package epermit.entities;

import java.time.LocalDateTime;
import java.util.UUID;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor // JPA
@Entity
@Table(name = "epermit_keys")
@SQLDelete(sql = "UPDATE epermit_keys SET deleted = '1' WHERE id = ?")
@Where(clause = "deleted = false")
public class PrivateKey {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
        name = "UUID",
        strategy = "org.hibernate.id.UUIDGenerator"
    )
    private UUID id;

    @Column(name = "key_id", nullable = false, unique = true)
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

