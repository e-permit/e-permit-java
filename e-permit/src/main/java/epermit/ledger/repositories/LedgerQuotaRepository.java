package epermit.ledger.repositories;

import org.springframework.stereotype.Repository;
import epermit.ledger.entities.LedgerQuota;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface LedgerQuotaRepository extends JpaRepository<LedgerQuota, Integer> {
    
}
