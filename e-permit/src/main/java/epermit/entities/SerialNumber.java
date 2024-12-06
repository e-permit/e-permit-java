package epermit.entities;

import java.time.LocalDateTime;
import java.util.UUID;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "epermit_serial_numbers")
@Builder
public class SerialNumber {
    @Id
    @UuidGenerator
    private UUID id;

    @Column(name = "permit_issuer", nullable = false)
    private String permitIssuer;

    @Column(name = "permit_issued_for", nullable = false)
    private String permitIssuedFor;

    @Column(name = "permit_year", nullable = false)
    private int permitYear;

    @Column(name = "permit_type", nullable = false)
    private Integer permitType;

    @Builder.Default
    @Column(name = "next_serial", nullable = false)
    private Long nextSerial = 1L;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}


