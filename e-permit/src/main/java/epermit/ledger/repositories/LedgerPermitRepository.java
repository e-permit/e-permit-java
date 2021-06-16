package epermit.ledger.repositories;

import org.springframework.stereotype.Repository;
import epermit.ledger.entities.LedgerPermit;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

@Repository
public interface LedgerPermitRepository
          extends JpaRepository<LedgerPermit, Long>, JpaSpecificationExecutor<LedgerPermit> {
     Optional<LedgerPermit> findOneByPermitId(String permitId);

     boolean existsByPermitId(String permitId);
}