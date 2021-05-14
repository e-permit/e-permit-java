package epermit.events.keycreated;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import epermit.events.EventFactoryUtil;
import epermit.events.EventType;

@ExtendWith(MockitoExtension.class)
public class KeyCreatedEventFactoryTest {

    @Mock EventFactoryUtil util;
    @InjectMocks KeyCreatedEventFactory factory;
    @Test
    void createTest() {
        KeyCreatedEvent event = factory.create("1", "UA");
        assertEquals(EventType.KEY_CREATED, event.getEventType());
        assertEquals("1", event.getKeyId());
        assertNotNull(event.getValidFrom());
        assertEquals("jws", event.getJwk());
    }
}
