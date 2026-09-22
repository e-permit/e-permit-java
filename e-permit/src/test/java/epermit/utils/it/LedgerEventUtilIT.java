package epermit.utils.it;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import epermit.commons.EpermitValidationException;
import epermit.commons.GsonUtil;
import epermit.entities.LedgerEvent;
import epermit.ledgerevents.LedgerEventBase;
import epermit.ledgerevents.LedgerEventHandler;
import epermit.ledgerevents.LedgerEventType;
import epermit.ledgerevents.LedgerEventUtil;
import epermit.repositories.LedgerEventRepository;

@Testcontainers
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Transactional
public class LedgerEventUtilIT {

    private static final String PRODUCER = "TR";
    private static final String CONSUMER = "UZ";

    private static final LedgerEventType EVENT_TYPE = LedgerEventType.QUOTA_CREATED;
    private static final String HANDLER_BEAN_NAME = "QUOTA_CREATED_EVENT_HANDLER";

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    @Autowired
    private LedgerEventRepository ledgerEventRepository;

    @Autowired
    private LedgerEventUtil ledgerEventUtil;

    @MockitoBean(name = HANDLER_BEAN_NAME)
    private LedgerEventHandler eventHandler;

    @Test
    void shouldPersistEventAndDelegateToHandler() {
        TestEvent event = createEvent("0");

        ledgerEventUtil.handleEvent(event, "proof-header.proof-signature");

        Optional<LedgerEvent> persisted = ledgerEventRepository.findOneByEventId(event.getEventId());
        assertTrue(persisted.isPresent(), "Ledger event should be persisted");

        LedgerEvent ledgerEvent = persisted.get();
        assertEquals(PRODUCER, ledgerEvent.getProducer());
        assertEquals(CONSUMER, ledgerEvent.getConsumer());
        assertEquals(EVENT_TYPE, ledgerEvent.getEventType());
        assertEquals("0", ledgerEvent.getPreviousEventId());
        assertEquals(event.getEventTimestamp(), ledgerEvent.getEventTimestamp());
        assertEquals("proof-header.proof-signature", ledgerEvent.getProof());

        assertEquals(event.getPayload(),
                GsonUtil.toMap(ledgerEvent.getEventContent()).get("payload"));

        ArgumentCaptor<LedgerEventBase> captor = ArgumentCaptor.forClass(LedgerEventBase.class);
        verify(eventHandler, times(1)).handle(captor.capture());
        assertEquals(event.getEventId(), captor.getValue().getEventId());
    }

    @Test
    void shouldFailWhenEventIdAlreadyExists() {
        TestEvent event = createEvent("0");
        seedLedgerEvent(CONSUMER, event.getEventId(), "some-other-previous-id");

        EpermitValidationException ex = assertThrows(EpermitValidationException.class,
                () -> ledgerEventUtil.handleEvent(event, "proof"));

        assertTrue(ex.getMessage().contains("EVENT_ALREADY_EXISTS"));
        assertEquals(1, ledgerEventRepository.count());
        verifyNoInteractions(eventHandler);
    }

    @Test
    void shouldFailWhenPreviousEventIdAlreadyUsed() {
        seedLedgerEvent(CONSUMER, UUID.randomUUID().toString(), "0");

        TestEvent fork = createEvent("0");

        EpermitValidationException ex = assertThrows(EpermitValidationException.class,
                () -> ledgerEventUtil.handleEvent(fork, "proof"));

        assertTrue(ex.getMessage().contains("PREVIOUS_EVENTID_USED"));
        assertTrue(ledgerEventRepository.findOneByEventId(fork.getEventId()).isEmpty());
        verifyNoInteractions(eventHandler);
    }


    @Test
    void shouldAllowSameEventIdOnAnotherConsumersLedger() {
        TestEvent event = createEvent("0");
        seedLedgerEvent("GE", event.getEventId(), "0");

        ledgerEventUtil.handleEvent(event, "proof");

        assertEquals(2, ledgerEventRepository.count());
        verify(eventHandler, times(1)).handle(any());
    }

    private TestEvent createEvent(String previousEventId) {
        return new TestEvent(PRODUCER, CONSUMER, previousEventId, EVENT_TYPE,
                "payload-" + UUID.randomUUID());
    }

    private void seedLedgerEvent(String consumer, String eventId, String previousEventId) {
        LedgerEvent ledgerEvent = new LedgerEvent();
        ledgerEvent.setEventId(eventId);
        ledgerEvent.setProducer(PRODUCER);
        ledgerEvent.setConsumer(consumer);
        ledgerEvent.setPreviousEventId(previousEventId);
        ledgerEvent.setEventType(EVENT_TYPE);
        ledgerEvent.setEventTimestamp(Instant.now().getEpochSecond());
        ledgerEvent.setEventContent("{}");
        ledgerEvent.setProof("seeded.proof");
        ledgerEventRepository.saveAndFlush(ledgerEvent);
    }

    // Minimal concrete event
    public static class TestEvent extends LedgerEventBase {
        private final String payload;

        public TestEvent(String producer, String consumer, String previousEventId,
                LedgerEventType eventType, String payload) {
            super(producer, consumer, previousEventId, eventType);
            this.payload = payload;
        }

        public String getPayload() {
            return payload;
        }
    }
}