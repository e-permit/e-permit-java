package epermit.events;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import epermit.common.PermitProperties;
import epermit.entities.CreatedEvent;
import epermit.repositories.CreatedEventRepository;

@ExtendWith(MockitoExtension.class)
public class EventFactoryUtilTest {
    @Mock CreatedEventRepository createdEventRepository;
    @Mock PermitProperties properties;
    @Test
    void createShouldWork() {
        CreatedEvent lastEvent = new CreatedEvent();
        lastEvent.setEventId("1");
        when(properties.getIssuerCode()).thenReturn("TR");
        when(createdEventRepository.findTopByIssuedForOrderByIdDesc("UA")).thenReturn(lastEvent);
        EventFactoryUtil util = new EventFactoryUtil(properties, createdEventRepository);
        DummyEvent event = new DummyEvent();
        util.setCommon(event, "UA");
        assertEquals("TR", event.getIssuer());
        assertEquals("UA", event.getIssuedFor());
        assertEquals("1", event.getPreviousEventId());
        assertNotNull(event.getCreatedAt());
        assertNotNull(event.getEventId());
    }

    class DummyEvent extends EventBase{

    }
}

