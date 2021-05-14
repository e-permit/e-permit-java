package epermit.data.entities;

import java.time.OffsetDateTime;
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
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

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

  @Column(name = "created_at", nullable = true)
  private OffsetDateTime createdAt;
  
  @OneToMany(cascade = CascadeType.ALL)
  @EqualsAndHashCode.Exclude
  private List<AuthorityKey> keys = new ArrayList<>();

  @JsonIgnore
  public void addKey(AuthorityKey key) {
    keys.add(key);
    key.setAuthority(this);
  }

  @OneToMany(cascade = CascadeType.ALL)
  @EqualsAndHashCode.Exclude
  private List<VerifierQuota> verifierQuotas = new ArrayList<>();

  @JsonIgnore
  public void addVerifierQuota(VerifierQuota verifierQuota) {
    verifierQuotas.add(verifierQuota);
    verifierQuota.setAuthority(this);
  }

  @OneToMany(cascade = CascadeType.ALL)
  @EqualsAndHashCode.Exclude
  private List<IssuerQuota> issuerQuotas = new ArrayList<>();

  @JsonIgnore
  public void addIssuerQuota(IssuerQuota quota) {
    issuerQuotas.add(quota);
    quota.setAuthority(this);
  }
 
}
