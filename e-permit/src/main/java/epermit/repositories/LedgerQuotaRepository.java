package epermit.repositories;

import org.springframework.stereotype.Repository;
import epermit.entities.LedgerQuota;
import epermit.models.enums.PermitType;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface LedgerQuotaRepository
                extends JpaRepository<LedgerQuota, UUID>, JpaSpecificationExecutor<LedgerQuota> {
        @Query("SELECT q FROM LedgerQuota q WHERE q.permitIssuer = :issuer and " +
                        "q.permitIssuedFor = :issuedFor and q.permitType=:permitType and q.permitYear=:permitYear")
        Optional<LedgerQuota> getQuota(@Param("issuer") String iss, @Param("issuedFor") String issFor,
                        @Param("permitType") PermitType typ, @Param("permitYear") Integer year);
}
