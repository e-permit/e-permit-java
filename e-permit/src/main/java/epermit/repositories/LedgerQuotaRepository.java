package epermit.repositories;

import org.springframework.stereotype.Repository;
import epermit.entities.LedgerQuota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

@Repository
public interface LedgerQuotaRepository
        extends JpaRepository<LedgerQuota, Integer>, JpaSpecificationExecutor<LedgerQuota> {

}
