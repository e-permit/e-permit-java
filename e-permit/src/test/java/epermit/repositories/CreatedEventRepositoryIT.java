package epermit.repositories;

import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Example;
import epermit.entities.CreatedEvent;
import epermit.events.EventType;

@DataJpaTest
public class CreatedEventRepositoryIT {
    @Autowired
    private CreatedEventRepository repository;

    @Test
    void saveTest(){
        CreatedEvent event = new CreatedEvent();
        event.setEventId("eventId");
        event.setEventType(EventType.KEY_CREATED);
        event.setIssuedFor("UZ");
        event.setJws("jws");
        event.setPreviousEventId("0");
        repository.save(event);
        Example<CreatedEvent> example = Example.of(event);
        Optional<CreatedEvent> r = repository.findOne(example);
        assertTrue(r.isPresent());
    }
    
}