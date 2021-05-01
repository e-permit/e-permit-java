package epermit.repositories;

import org.springframework.stereotype.Repository;
import epermit.entities.CreatedEvent;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface CreatedEventRepository extends JpaRepository<CreatedEvent, Long> {
    Optional<CreatedEvent> findOneByEventId(String eventId);
    CreatedEvent findTopByOrderByIdDesc();
    //List<CreatedEvent> findFirst10ByState(EventState state);
}
