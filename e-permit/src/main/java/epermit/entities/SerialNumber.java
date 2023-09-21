package epermit.entities;

import java.time.LocalDateTime;
import java.util.UUID;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import epermit.models.enums.SerialNumberState;
import epermit.models.enums.PermitType;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor // JPA
@Entity
@Table(name = "epermit_serial_numbers")
public class SerialNumber {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
        name = "UUID",
        strategy = "org.hibernate.id.UUIDGenerator"
    )
    private UUID id;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false)
    private SerialNumberState state;

    @Column(name = "authority_code", nullable = false)
    private String authorityCode;

    @Column(name = "serial_number", nullable = false)
    private int serialNumber;

    @Column(name = "permit_year", nullable = false)
    private int permitYear;

    @Column(name = "permit_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private PermitType permitType;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
