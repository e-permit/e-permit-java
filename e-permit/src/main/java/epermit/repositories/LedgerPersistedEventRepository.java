package epermit.repositories;

import org.springframework.stereotype.Repository;
import epermit.entities.LedgerPersistedEvent;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

@Repository
public interface LedgerPersistedEventRepository extends JpaRepository<LedgerPersistedEvent, Long>,
                JpaSpecificationExecutor<LedgerPersistedEvent> {

        Optional<LedgerPersistedEvent> findOneByEventId(String eventId);

        Optional<LedgerPersistedEvent> findOneByIssuerAndIssuedForAndEventId(String issuer,
                        String issuedFor, String eventId);

        Optional<LedgerPersistedEvent> findTopByIssuerAndIssuedForOrderByIdDesc(String issuer,
                        String issuedFor);

        boolean existsByIssuerAndIssuedForAndEventId(String issuer, String issuedFor,
                        String eventId);

        boolean existsByIssuerAndIssuedFor(String issuer, String issuedFor);

        boolean existsByIssuerAndIssuedForAndPreviousEventId(String issuer, String issuedFor,
                        String previousEventId);
}
