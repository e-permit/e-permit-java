package epermit.events.keycreated;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import epermit.common.EventType;
import epermit.entities.Key;
import epermit.events.EventFactoryUtil;

@ExtendWith(MockitoExtension.class)
public class KeyCreatedEventFactoryTest {

    @Mock EventFactoryUtil util; 
    @Test
    void createShouldWork() {
        KeyCreatedEventFactory factory = new KeyCreatedEventFactory(util);
        Key key = new Key();
        key.setKid("1");
        key.setContent("jws");
        KeyCreatedEvent event = factory.create(key, "UA");
        assertEquals(EventType.KEY_CREATED, event.getEventType());
        assertEquals("1", event.getKeyId());
        assertNotNull(event.getValidFrom());
        assertEquals("jws", event.getJwk());
    }
}
