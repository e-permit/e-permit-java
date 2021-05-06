package epermit.events.permitrevoked;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import epermit.common.EventType;
import epermit.entities.IssuedPermit;
import epermit.events.EventFactoryUtil;

@ExtendWith(MockitoExtension.class)
public class PermitRevokedEventFactoryTest {
    @Mock EventFactoryUtil util; 
    @Test
    void createShouldWork() {
        PermitRevokedEventFactory factory = new PermitRevokedEventFactory(util);
        IssuedPermit permit = new IssuedPermit();
        PermitRevokedEvent event = factory.create(permit);
        assertEquals(EventType.PERMIT_REVOKED, event.getEventType());
        assertEquals(permit.getPermitId(), event.getPermitId());
    }
}
