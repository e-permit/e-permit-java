package epermit.repositories;

import org.springframework.stereotype.Repository;
import epermit.entities.LedgerEvent;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

@Repository
public interface LedgerEventRepository extends JpaRepository<LedgerEvent, UUID>,
                JpaSpecificationExecutor<LedgerEvent> {

        Optional<LedgerEvent> findOneByEventId(String eventId);

        Optional<LedgerEvent> findOneByProducerAndConsumerAndEventId(String producer,
                        String consumer, String eventId);

        Optional<LedgerEvent> findTopByProducerAndConsumerOrderByCreatedAtDesc(String producer,
                        String consumer);

        boolean existsByProducerAndConsumerAndEventId(String producer, String consumer,
                        String eventId);

        boolean existsByProducerAndConsumer(String producer, String consumer);

        boolean existsByProducerAndConsumerAndPreviousEventId(String producer, String consumer,
                        String previousEventId);
}
