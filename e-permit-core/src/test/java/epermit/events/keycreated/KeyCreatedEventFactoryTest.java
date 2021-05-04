package epermit.events.keycreated;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import epermit.common.EventType;
import epermit.common.PermitProperties;
import epermit.entities.Key;
import epermit.repositories.AuthorityRepository;
import epermit.repositories.CreatedEventRepository;
import epermit.services.KeyService;
import lombok.extern.slf4j.Slf4j;

@ExtendWith(MockitoExtension.class)
@Slf4j
public class KeyCreatedEventFactoryTest {

    @Mock
    AuthorityRepository authorityRepository;
    @Mock
    PermitProperties props;
    @Mock
    CreatedEventRepository createdEventRepository;
    @Mock
    KeyService keyService;

    @Test
    void createShouldWork() {
        KeyCreatedEventFactory factory = new KeyCreatedEventFactory();
        Key key = keyService.create("1");
        KeyCreatedEvent event = factory.create(key);
        assertEquals(EventType.KEY_CREATED, event.getEventType());
  
    }
}
