package epermit.repositories;


import org.springframework.stereotype.Repository;
import epermit.entities.IssuerQuota;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface IssuerQuotaRepository extends JpaRepository<IssuerQuota, Integer> {
    
}