package epermit.repositories;

import org.springframework.stereotype.Repository;
import epermit.entities.ReceivedEvent;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface ReceivedEventRepository extends JpaRepository<ReceivedEvent, Long> {
     //List<ReceivedMessage> findFirst10ByHandledFalse();
}
