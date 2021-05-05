package epermit.events.quotacreated;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import epermit.common.EventType;
import epermit.entities.VerifierQuota;
import epermit.events.EventFactoryUtil;

@ExtendWith(MockitoExtension.class)
public class QuotaCreatedEventFactoryTest {
    @Mock EventFactoryUtil util;
    @Test
    void createShouldWork() {
        QuotaCreatedEventFactory factory = new QuotaCreatedEventFactory(util);
        VerifierQuota quota = new VerifierQuota();
        QuotaCreatedEvent event = factory.create(quota);
        assertEquals(EventType.QUOTA_CREATED, event.getEventType());
    }
}
