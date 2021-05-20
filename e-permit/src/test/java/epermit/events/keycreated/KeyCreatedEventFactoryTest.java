package epermit.events.keycreated;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import java.time.OffsetDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import epermit.entities.Key;
import epermit.events.EventFactoryUtil;
import epermit.events.EventType;
import epermit.repositories.KeyRepository;

@ExtendWith(MockitoExtension.class)
public class KeyCreatedEventFactoryTest {

    @Mock
    EventFactoryUtil util;

    @Mock
    KeyRepository keyRepository;

    @InjectMocks
    KeyCreatedEventFactory factory;

    @Test
    void createTest() {
        Key key = new Key();
        key.setPublicJwk("publicJwk");
        key.setKeyId("1");
        key.setValidFrom(OffsetDateTime.now().toEpochSecond());
        KeyCreatedEvent event = factory.create(key, "UA");
        assertEquals(EventType.KEY_CREATED, event.getEventType());
        assertEquals("1", event.getKeyId());
        assertNotNull(event.getValidFrom());
        assertEquals("publicJwk", event.getJwk());
    }
}
