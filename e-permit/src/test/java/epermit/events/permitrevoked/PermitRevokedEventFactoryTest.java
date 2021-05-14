package epermit.events.permitrevoked;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import epermit.events.EventFactoryUtil;
import epermit.events.EventType;

@ExtendWith(MockitoExtension.class)
public class PermitRevokedEventFactoryTest {
    @Mock EventFactoryUtil util; 
    @InjectMocks PermitRevokedEventFactory factory;
    @Test
    void createTest() {
        PermitRevokedEvent event = factory.create("UA", "TR-UA");
        assertEquals(EventType.PERMIT_REVOKED, event.getEventType());
        assertEquals("TR-UA", event.getPermitId());
    }
}
