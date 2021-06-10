package epermit.events.keyrevoked;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import epermit.events.EventFactoryUtil;
import epermit.events.EventType;

@ExtendWith(MockitoExtension.class)
public class KeyRevokedEventFactoryTest {
    @Mock
    EventFactoryUtil util;
    
    @InjectMocks
    KeyRevokedEventFactory factory;

    @Test
    void createTest() {
        KeyRevokedEvent event = factory.create("1", "TR");
        assertEquals(EventType.KEY_REVOKED, event.getEventType());
        assertEquals("1", event.getKeyId());
    }
}
