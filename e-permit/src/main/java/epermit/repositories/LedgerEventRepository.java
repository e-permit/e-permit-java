package epermit.repositories;

import org.springframework.stereotype.Repository;
import epermit.entities.LedgerEvent;
import jakarta.persistence.LockModeType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface LedgerEventRepository extends JpaRepository<LedgerEvent, UUID>,
                JpaSpecificationExecutor<LedgerEvent> {
        @Lock (LockModeType.PESSIMISTIC_WRITE)   // SELECT ... FOR UPDATE
        @Query("""
                select e from LedgerEvent e
                           where e.producer = :producer and e.consumer = :consumer and e.eventId = :eventId
                           """)
        Optional<LedgerEvent> lockEvent(@Param("producer") String producer,
                                        @Param("consumer") String consumer,
                                        @Param("eventId") String eventId);
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

        @Query("SELECT le FROM LedgerEvent le JOIN CreatedEvent ce ON le.eventId = ce.eventId " +
                        "WHERE le.producer = :producer AND le.consumer = :consumer AND ce.sent = false")
        List<LedgerEvent> findAllPendingEvents(@Param("producer") String producer,
                        @Param("consumer") String consumer);
}
