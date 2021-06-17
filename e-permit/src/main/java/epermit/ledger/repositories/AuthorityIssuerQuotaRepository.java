package epermit.ledger.repositories;

import org.springframework.stereotype.Repository;
import epermit.ledger.entities.AuthorityIssuerQuota;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface AuthorityIssuerQuotaRepository extends JpaRepository<AuthorityIssuerQuota, Integer> {
     
}