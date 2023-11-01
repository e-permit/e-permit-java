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
        @Query("SELECT q FROM LedgerQuota q WHERE q.issuer = :issuer and " +
                        "q.issued_for = :issued_for and q.permit_type=:permit_type and q.permit_year=:permit_year")
        Optional<LedgerQuota> getQuota(@Param("issuer") String iss, @Param("issued_for") String issFor,
                        @Param("permit_type") PermitType typ, @Param("permit_year") Integer year);
}
