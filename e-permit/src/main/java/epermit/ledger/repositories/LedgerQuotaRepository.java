package epermit.ledger.repositories;

import org.springframework.stereotype.Repository;
import epermit.ledger.entities.LedgerQuota;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface LedgerQuotaRepository extends JpaRepository<LedgerQuota, Integer> {
}
