package epermit.repositories;

import org.springframework.stereotype.Repository;
import epermit.entities.CreatedEvent;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface CreatedEventRepository
        extends JpaRepository<CreatedEvent, Long> {
      Optional<CreatedEvent> findByEventId(String eventId);
}

