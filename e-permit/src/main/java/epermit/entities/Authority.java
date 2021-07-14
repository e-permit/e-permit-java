package epermit.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import epermit.models.enums.AuthenticationType;
import epermit.models.enums.PermitType;
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

  @Column(name = "authentication_type", nullable = false)
  @Enumerated(EnumType.STRING)
  private AuthenticationType authenticationType;

  @Column(name = "last_sended_event_id", nullable = false)
  private String lastSendedEventId;

  @OneToMany(cascade = CascadeType.ALL)
  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  private List<AuthorityIssuerQuota> issuerQuotas = new ArrayList<>();

  @CreationTimestamp
  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  public Optional<AuthorityIssuerQuota> getIssuerQuota(PermitType type, int year) {
    return issuerQuotas.stream().filter(x -> x.getPermitType() == type && x.getPermitYear() == year)
        .findFirst();
  }
}
