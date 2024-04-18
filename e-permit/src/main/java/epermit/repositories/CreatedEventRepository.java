package epermit.repositories;

import org.springframework.stereotype.Repository;
import epermit.entities.CreatedEvent;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

@Repository
public interface CreatedEventRepository
    extends JpaRepository<CreatedEvent, UUID> {
  Optional<CreatedEvent> findByEventId(String eventId);

  List<CreatedEvent> findAllBySentFalseOrderByCreatedAtAsc();

  /*@Query("SELECT count(*)>0 FROM CreatedEvent ce, LedgerEvent le " +
      "WHERE ce.eventId = le.id and ce.error IS NOT NULL and le.consumer = :consumer")
  boolean errorExistsByConsumer(@Param("consumer") String consumer);*/
}
