package epermit.repositories;

import org.springframework.stereotype.Repository;
import epermit.entities.LedgerPermit;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

@Repository
public interface LedgerPermitRepository
          extends JpaRepository<LedgerPermit, UUID>, JpaSpecificationExecutor<LedgerPermit> {
     Optional<LedgerPermit> findOneByPermitId(String permitId);

     boolean existsByPermitId(String permitId);
}