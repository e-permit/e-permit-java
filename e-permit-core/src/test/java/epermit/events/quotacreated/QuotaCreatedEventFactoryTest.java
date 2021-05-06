package epermit.events.quotacreated;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import epermit.common.EventType;
import epermit.common.PermitType;
import epermit.entities.Authority;
import epermit.entities.VerifierQuota;
import epermit.events.EventFactoryUtil;

@ExtendWith(MockitoExtension.class)
public class QuotaCreatedEventFactoryTest {
    @Mock EventFactoryUtil util;
    @Test
    void createShouldWork() {
        QuotaCreatedEventFactory factory = new QuotaCreatedEventFactory(util);
        VerifierQuota quota = new VerifierQuota();
        Authority authority = new Authority();
        authority.setCode("UA");
        quota.setAuthority(authority);
        quota.setPermitType(PermitType.BILITERAL);
        quota.setPermitYear(2121);
        quota.setStartNumber(1);
        QuotaCreatedEvent event = factory.create(quota);
        assertEquals(EventType.QUOTA_CREATED, event.getEventType());
        assertEquals(quota.getEndNumber(), event.getEndNumber());
        assertEquals(quota.getPermitType(), event.getPermitType());
        assertEquals(quota.getPermitYear(), event.getPermitYear());
        assertEquals(quota.getStartNumber(), event.getStartNumber());
    }
}
