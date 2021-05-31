package epermit.events;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import epermit.entities.Authority;
import epermit.entities.CreatedEvent;
import epermit.models.EPermitProperties;
import epermit.repositories.AuthorityRepository;
import epermit.repositories.CreatedEventRepository;
import epermit.utils.JwsUtil;

@ExtendWith(MockitoExtension.class)
public class EventFactoryUtilTest {
    @Mock
    EPermitProperties properties;

    @Mock
    CreatedEventRepository createdEventRepository;

    @Mock
    ApplicationEventPublisher eventPublisher;

    @Mock
    JwsUtil jwsUtil;

    @Mock
    AuthorityRepository authorityRepository;

    @InjectMocks
    EventFactoryUtil util;

    @Test
    void saveAndPublishTest() {
        CreatedEvent lasEvent = new CreatedEvent();
        lasEvent.setEventId("0");
        when(properties.getIssuerCode()).thenReturn("TR");
        when(createdEventRepository.findTopByIssuedForOrderByIdDesc("UA"))
                .thenReturn(Optional.of(lasEvent));
        when(jwsUtil.createJws(any())).thenReturn("jws");
        when(authorityRepository.findOneByCode(anyString())).thenReturn(new Authority());
        DummyEvent event = new DummyEvent();
        util.saveAndPublish(event, "UA");
        assertEquals("TR", event.getIssuer());
        assertEquals("UA", event.getIssuedFor());
        assertEquals("0", event.getPreviousEventId());
        assertNotNull(event.getCreatedAt());
        assertNotNull(event.getEventId());
    }

    class DummyEvent extends EventBase {

    }
}

