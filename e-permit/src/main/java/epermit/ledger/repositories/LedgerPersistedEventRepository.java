package epermit.ledger.repositories;

import org.springframework.stereotype.Repository;
import epermit.ledger.entities.LedgerPersistedEvent;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface LedgerPersistedEventRepository extends JpaRepository<LedgerPersistedEvent, Long> {
    Optional<LedgerPersistedEvent> findOneByIssuerAndIssuedForAndEventId(String issuer,
            String issuedFor, String eventId);

    Optional<LedgerPersistedEvent> findTopByIssuerAndIssuedForOrderByIdDesc(String issuer,
            String issuedFor);

    List<LedgerPersistedEvent> findByIdGreaterThanOrderByIdAsc(Long id);

    boolean existsByIssuerAndIssuedForAndEventId(String issuer, String issuedFor, String eventId);

    boolean existsByIssuerAndIssuedFor(String issuer, String issuedFor);
}
