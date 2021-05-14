package epermit.data.repositories;


import org.springframework.stereotype.Repository;
import epermit.data.entities.VerifierQuota;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface VerifierQuotaRepository extends JpaRepository<VerifierQuota, Integer> {
    
}
