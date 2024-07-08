package epermit.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import epermit.commons.EpermitValidationException;
import epermit.commons.ErrorCodes;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor 
@Entity
@Table(name = "epermit_authorities")
public class Authority {

  @Id
  @GeneratedValue(generator = "UUID")
  @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
  private UUID id;

  @Column(name = "code", nullable = false, unique = true)
  private String code;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "public_api_uri", nullable = false, length = 1000)
  private String publicApiUri;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  @OneToMany(cascade = CascadeType.ALL, mappedBy = "authority")
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  private List<AuthorityKey> keys = new ArrayList<>();

  @JsonIgnore
  public void addKey(AuthorityKey key) {
    keys.add(key);
    key.setAuthority(this);
  }

  @JsonIgnore
  public List<AuthorityKey> getValidKeys() {
    return this.getKeys().stream()
        .filter(k -> !k.isRevoked()).toList();
  }

  @JsonIgnore
  public AuthorityKey getValidKeyById(String keyId) {
    return this.getValidKeys().stream()
        .filter(k -> k.getKeyId().equals(keyId))
        .findFirst()
        .orElseThrow(() -> new EpermitValidationException(ErrorCodes.KEY_NOTFOUND));
  }
}
