package epermit.repositories;

import org.springframework.stereotype.Repository;
import epermit.common.EventState;
import epermit.entities.CreatedEvent;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface CreatedEventRepository extends JpaRepository<CreatedEvent, Long> {
    Optional<CreatedEvent> findOneByEventId(String eventId);
    List<CreatedEvent> findFirst10ByState(EventState state);
}
