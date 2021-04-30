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

    @Column(name = "kid", nullable = false)
    private String kid;
    
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "enabled", nullable = false)
    private Boolean enabled;

    @Column(name = "salt", nullable = false)
    private String salt;

    @Column(name = "content", nullable = false, length=4000)
    private String content;
}
