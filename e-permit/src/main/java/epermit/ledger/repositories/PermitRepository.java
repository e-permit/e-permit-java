package epermit.ledger.repositories;

import org.springframework.stereotype.Repository;
import epermit.ledger.entities.Permit;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

@Repository
public interface PermitRepository
          extends JpaRepository<Permit, Long>, JpaSpecificationExecutor<Permit> {
     Optional<Permit> findOneByIssuedForAndPermitId(String issuedFor, String permitId);

     Optional<Permit> findFirstByIssuedForAndRevokedTrue(String issuedFor);

     boolean existsByIssuedForAndPermitId(String issuer, String permitId);
}