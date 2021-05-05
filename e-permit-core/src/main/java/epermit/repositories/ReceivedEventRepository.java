package epermit.repositories;

import org.springframework.stereotype.Repository;
import epermit.entities.ReceivedEvent;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface ReceivedEventRepository extends JpaRepository<ReceivedEvent, Long> {
     Optional<ReceivedEvent> findOneByIssuerAndEventId(String issuer, String eventId);
}
