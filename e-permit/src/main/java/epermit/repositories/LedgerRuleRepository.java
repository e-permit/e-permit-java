package epermit.repositories;

import org.springframework.stereotype.Repository;
import epermit.entities.LedgerRule;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface LedgerRuleRepository extends JpaRepository<LedgerRule, Integer> {
}
