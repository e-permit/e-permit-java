package epermit.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import epermit.entities.CreatedEvent;
import epermit.entities.LedgerEvent;
import epermit.ledgerevents.LedgerEventType;
import lombok.extern.slf4j.Slf4j;

@Testcontainers
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class LedgerEventRepositoryIT {
    @Autowired
    private LedgerEventRepository repository;

    @Autowired
    private CreatedEventRepository crepository;

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            "postgres:15-alpine");

    @Test
    void findAllPendingEventsTest() {
        String eventId = UUID.randomUUID().toString();
        LedgerEvent event = new LedgerEvent();
        event.setConsumer("B");
        event.setEventId(eventId);
        event.setEventContent("null");
        event.setEventTimestamp(0L);
        event.setEventType(LedgerEventType.PERMIT_CREATED);
        event.setPreviousEventId("0");
        event.setProducer("A");
        event.setProof("null");
        repository.save(event);
        CreatedEvent cEvent = new CreatedEvent();
        cEvent.setEventId(eventId);
        cEvent.setSent(false);
        crepository.save(cEvent);
        List<LedgerEvent> events = repository.findAllPendingEvents("A", "B");
        assertEquals(1, events.size());
        System.out.println("=== Pending Events ===");
        for (LedgerEvent e : events) {
            System.out.println(e);
        }
    }
}
