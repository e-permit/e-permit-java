package epermit.events.permitcreated;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import epermit.common.EventType;
import epermit.entities.IssuedPermit;
import epermit.events.EventFactoryUtil;

@ExtendWith(MockitoExtension.class)
public class PermitCreatedEventFactoryTest {
    @Mock EventFactoryUtil util; 
    @Test
    void createShouldWork() {
        PermitCreatedEventFactory factory = new PermitCreatedEventFactory(util);
        IssuedPermit permit = new IssuedPermit();
        PermitCreatedEvent event = factory.create(permit);
        assertEquals(EventType.PERMIT_CREATED, event.getEventType());
    }
}
