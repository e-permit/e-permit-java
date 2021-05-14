package epermit.data.repositories;


import org.springframework.stereotype.Repository;
import epermit.data.entities.IssuerQuota;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface IssuerQuotaRepository extends JpaRepository<IssuerQuota, Integer> {
    
}