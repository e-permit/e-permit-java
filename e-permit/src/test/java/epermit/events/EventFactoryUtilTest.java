package epermit.events;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import epermit.models.EPermitProperties;
import epermit.services.EventService;

@ExtendWith(MockitoExtension.class)
public class EventFactoryUtilTest {
    @Mock
    EPermitProperties properties;

    @Mock
    EventService eventService;

    @InjectMocks
    EventFactoryUtil util;

    @Test
    void createShouldWork() {
        when(properties.getIssuerCode()).thenReturn("TR");
        when(eventService.getSendedLastEventId("UA")).thenReturn("1");
        DummyEvent event = new DummyEvent();
        util.setCommon(event, "UA");
        assertEquals("TR", event.getIssuer());
        assertEquals("UA", event.getIssuedFor());
        assertEquals("1", event.getPreviousEventId());
        assertNotNull(event.getCreatedAt());
        assertNotNull(event.getEventId());
    }

    class DummyEvent extends EventBase {

    }
}

