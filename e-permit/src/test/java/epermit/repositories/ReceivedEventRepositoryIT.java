package epermit.repositories;

import static org.junit.jupiter.api.Assertions.assertTrue;
import java.time.OffsetDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Example;
import epermit.entities.ReceivedEvent;
import epermit.events.EventType;

@DataJpaTest
public class ReceivedEventRepositoryIT {
    @Autowired
    private ReceivedEventRepository repository;

    @Test
    void saveTest(){
        ReceivedEvent event = new ReceivedEvent();
        event.setEventId("eventId");
        event.setEventType(EventType.KEY_CREATED);
        event.setIssuer("UZ");
        event.setJws("jws");
        event.setPreviousEventId("0");
        repository.save(event);
        Example<ReceivedEvent> example = Example.of(event);
        Optional<ReceivedEvent> r = repository.findOne(example);
        assertTrue(r.isPresent());
    }
    
}
