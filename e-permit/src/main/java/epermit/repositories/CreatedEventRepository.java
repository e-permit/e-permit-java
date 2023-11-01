package epermit.repositories;

import org.springframework.stereotype.Repository;
import epermit.entities.CreatedEvent;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface CreatedEventRepository
        extends JpaRepository<CreatedEvent, UUID> {
      Optional<CreatedEvent> findByEventId(String eventId);
      List<CreatedEvent> findAllBySentFalseOrderByCreatedAtAsc();
}

