package epermit.repositories;

import org.springframework.stereotype.Repository;
import epermit.entities.AuthorityEvent;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface AuthorityEventRepository
        extends JpaRepository<AuthorityEvent, Long> {
      Optional<AuthorityEvent> findByEventId(String eventId);
}

