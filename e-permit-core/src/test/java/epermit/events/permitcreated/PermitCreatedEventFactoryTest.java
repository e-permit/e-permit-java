package epermit.events.permitcreated;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import epermit.common.EventType;
import epermit.common.PermitType;
import epermit.entities.IssuedPermit;
import epermit.events.EventFactoryUtil;

@ExtendWith(MockitoExtension.class)
public class PermitCreatedEventFactoryTest {
    @Mock EventFactoryUtil util;

    @Test
    void createShouldWork() {
        PermitCreatedEventFactory factory = new PermitCreatedEventFactory(util);
        IssuedPermit permit = new IssuedPermit();
        permit.setCompanyName("companyName");
        permit.setIssuedFor("UA");
        permit.setPermitType(PermitType.BILITERAL);
        permit.setPermitYear(2021);
        permit.setPlateNumber("plateNumber");
        permit.setSerialNumber(1);
        permit.setPermitId("TR-UA-2021-1-1");
        PermitCreatedEvent event = factory.create(permit);
        assertEquals(EventType.PERMIT_CREATED, event.getEventType());
        assertEquals(PermitType.BILITERAL, event.getPermitType());
        assertEquals(2021, event.getPermitYear());
        assertEquals("plateNumber", event.getPlateNumber());
        assertEquals(1, event.getSerialNumber());
        assertEquals("TR-UA-2021-1-1", event.getPermitId());
    }
}
