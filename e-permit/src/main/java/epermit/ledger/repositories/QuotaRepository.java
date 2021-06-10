package epermit.ledger.repositories;


import org.springframework.stereotype.Repository;
import epermit.ledger.entities.Quota;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface QuotaRepository extends JpaRepository<Quota, Integer> {
}
