package epermit.ledger.repositories;

import org.springframework.stereotype.Repository;
import epermit.ledger.entities.LedgerRule;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface LedgerRuleRepository extends JpaRepository<LedgerRule, Integer> {
}
