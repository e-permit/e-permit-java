package epermit.repositories;


import org.springframework.stereotype.Repository;
import epermit.entities.VerifierQuota;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface VerifierQuotaRepository extends JpaRepository<VerifierQuota, Integer> {
    
}
