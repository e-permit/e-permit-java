package epermit.events.quotacreated;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import epermit.events.EventFactoryUtil;
import epermit.events.EventType;
import epermit.models.PermitType;

@ExtendWith(MockitoExtension.class)
public class QuotaCreatedEventFactoryTest {
    @Mock
    EventFactoryUtil util;

    @InjectMocks
    QuotaCreatedEventFactory factory;

    @Test
    void createTest() {
        QuotaCreatedEventInput input = new QuotaCreatedEventInput();
        input.setEndNumber(4);
        input.setIssuedFor("UA");
        input.setPermitType(PermitType.BILITERAL);
        input.setPermitYear(2021);
        input.setStartNumber(1);
        QuotaCreatedEvent event = factory.create(input);
        assertEquals(EventType.QUOTA_CREATED, event.getEventType());
        assertEquals(input.getEndNumber(), event.getEndNumber());
        assertEquals(input.getPermitType(), event.getPermitType());
        assertEquals(input.getPermitYear(), event.getPermitYear());
        assertEquals(input.getStartNumber(), event.getStartNumber());
    }
}
