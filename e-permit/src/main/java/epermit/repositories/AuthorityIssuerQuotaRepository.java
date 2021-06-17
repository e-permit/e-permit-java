package epermit.repositories;

import org.springframework.stereotype.Repository;
import epermit.entities.AuthorityIssuerQuota;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface AuthorityIssuerQuotaRepository extends JpaRepository<AuthorityIssuerQuota, Integer> {
     
}