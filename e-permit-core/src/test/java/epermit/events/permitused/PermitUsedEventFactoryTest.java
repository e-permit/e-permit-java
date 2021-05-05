package epermit.events.permitused;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import epermit.common.EventType;
import epermit.common.PermitActivityType;
import epermit.entities.Permit;
import epermit.events.EventFactoryUtil;

@ExtendWith(MockitoExtension.class)
public class PermitUsedEventFactoryTest {
    @Mock EventFactoryUtil util;
    @Test
    void createShouldWork() {
        PermitUsedEventFactory factory = new PermitUsedEventFactory(util);
        Permit permit = new Permit();
        PermitUsedEvent event = factory.create(permit, PermitActivityType.ENTERANCE);
        assertEquals(EventType.PERMIT_USED, event.getEventType());
    }
}
