package epermit.ledger.repositories;

import org.springframework.stereotype.Repository;
import epermit.ledger.entities.LedgerPersistedEvent;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface LedgerPersistedEventRepository extends JpaRepository<LedgerPersistedEvent, Long> {
    Optional<LedgerPersistedEvent> findOneByEventIdAndIssuedFor(String eventId, String issuedFor);

    Optional<LedgerPersistedEvent> findTopByIssuedForOrderByIdDesc(String issuedFor);

    List<LedgerPersistedEvent> findByIdGreaterThanOrderByIdAsc(Long id);
}
