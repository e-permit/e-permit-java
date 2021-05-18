package epermit.events.quotacreated;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import epermit.entities.Authority;
import epermit.entities.VerifierQuota;
import epermit.events.EventFactoryUtil;
import epermit.events.EventType;
import epermit.models.enums.PermitType;

@ExtendWith(MockitoExtension.class)
public class QuotaCreatedEventFactoryTest {
    @Mock
    EventFactoryUtil util;

    @InjectMocks
    QuotaCreatedEventFactory factory;

    @Test
    void createTest() {
        VerifierQuota input = new VerifierQuota();
        Authority authority = new Authority();
        authority.setCode("UA");
        input.setEndNumber(4);
        input.setAuthority(authority);
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
