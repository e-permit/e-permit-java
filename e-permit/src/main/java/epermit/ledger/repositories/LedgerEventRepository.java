package epermit.ledger.repositories;

import org.springframework.stereotype.Repository;
import epermit.ledger.entities.LedgerEvent;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface LedgerEventRepository extends JpaRepository<LedgerEvent, Long> {
    Optional<LedgerEvent> findOneByEventIdAndIssuedFor(String eventId, String issuedFor);

    Optional<LedgerEvent> findTopByIssuedForOrderByIdDesc(String issuedFor);

    List<LedgerEvent> findByIdGreaterThanOrderByIdAsc(Long id);
}
