package epermit.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
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
@Table(name = "authorities")
public class Authority {

  @Id
  @GeneratedValue
  private int id;

  @Column(name = "code", nullable = false)
  private String code;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "api_uri", nullable = false)
  private String apiUri;

  @Column(name = "verify_uri", nullable = false)
  private String verifyUri;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;
  
  @OneToMany(cascade = CascadeType.ALL)
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  private List<AuthorityKey> keys = new ArrayList<>();

  @JsonIgnore
  public void addKey(AuthorityKey key) {
    keys.add(key);
    key.setAuthority(this);
  }

  @OneToMany(cascade = CascadeType.ALL)
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  private List<VerifierQuota> verifierQuotas = new ArrayList<>();

  @JsonIgnore
  public void addVerifierQuota(VerifierQuota verifierQuota) {
    verifierQuotas.add(verifierQuota);
    verifierQuota.setAuthority(this);
  }

  @OneToMany(cascade = CascadeType.ALL)
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  private List<IssuerQuota> issuerQuotas = new ArrayList<>();

  @JsonIgnore
  public void addIssuerQuota(IssuerQuota quota) {
    issuerQuotas.add(quota);
    quota.setAuthority(this);
  }
 
}
