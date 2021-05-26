package epermit.entities;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

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

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
  
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "active", nullable = false)
    private boolean active;

    @ManyToOne
    @JoinColumn(name = "authority_id")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonIgnore // Avoid infinite loops
    private Authority authority;
}
