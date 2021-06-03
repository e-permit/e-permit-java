package epermit.events.permitused;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import epermit.entities.Permit;
import epermit.entities.PermitActivity;
import epermit.events.EventFactoryUtil;
import epermit.events.EventType;
import epermit.models.enums.PermitActivityType;

@ExtendWith(MockitoExtension.class)
public class PermitUsedEventFactoryTest {
    @Mock
    EventFactoryUtil util;

    @InjectMocks 
    PermitUsedEventFactory factory;

    @Test
    void createTest() {
        Permit permit = new Permit();
        permit.setPermitId("TR-UA");
        PermitActivity activity = new PermitActivity();
        activity.setPermit(permit);
        activity.setActivityTimestamp(Instant.now().getEpochSecond());
        PermitUsedEvent event = factory.create(activity);
        assertEquals(EventType.PERMIT_USED, event.getEventType());
        assertEquals("TR-UA", event.getPermitId());
        assertEquals(PermitActivityType.ENTERANCE, event.getActivityType());
    }
}
