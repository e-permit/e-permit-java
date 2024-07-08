package epermit.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import epermit.commons.QuotaEventListConverter;
import epermit.models.dtos.QuotaEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "epermit_ledger_quotas")
@Builder
public class LedgerQuota {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
        name = "UUID",
        strategy = "org.hibernate.id.UUIDGenerator"
    )
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
    @Column(name = "total_quota", nullable = false)
    private Long totalQuota = 0L;

    @Builder.Default
    @Column(name = "issued_count", nullable = false)
    private Long issuedCount = 0L;

    @Builder.Default
    @Column(name = "revoked_count", nullable = false)
    private Long revokedCount = 0L;

    @Builder.Default
    @Column(name = "events", nullable = false, length = 1000)
    @Convert(converter = QuotaEventListConverter.class)
    private List<QuotaEvent> events = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}


