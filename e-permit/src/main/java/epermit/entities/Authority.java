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
import org.hibernate.annotations.UpdateTimestamp;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor // JPA
@Entity
@Table(name = "epermit_authorities")
public class Authority {

  @Id
  @GeneratedValue(generator = "UUID")
  @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
  private UUID id;

  @Column(name = "code", nullable = false)
  private String code;

  @Column(name = "name", nullable = false)
  private String name;

  // @Column(name = "is_xroad", nullable = false)
  // private boolean xroad = false;

  // https://public-api or https://security-server/r1/{client}/Permit
  @Column(name = "public_api_uri", nullable = false)
  private String publicApiUri;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

}
