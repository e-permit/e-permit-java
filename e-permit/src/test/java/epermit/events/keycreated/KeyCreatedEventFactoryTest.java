package epermit.events.keycreated;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import epermit.entities.Key;
import epermit.events.EventFactoryUtil;
import epermit.events.EventType;
import epermit.models.dtos.PublicJwk;
import epermit.repositories.KeyRepository;
import epermit.utils.GsonUtil;

@ExtendWith(MockitoExtension.class)
public class KeyCreatedEventFactoryTest {

    @Mock
    EventFactoryUtil util;

    @Mock
    KeyRepository keyRepository;

    @InjectMocks
    KeyCreatedEventFactory factory;

    @Test
    void createTest() {
        Key key = new Key();
        PublicJwk jwk = new PublicJwk();
        jwk.setKid("1");
        key.setPublicJwk(GsonUtil.getGson().toJson(jwk));
        key.setKeyId("1");
        KeyCreatedEvent event = factory.create(key, "UA");
        assertEquals(EventType.KEY_CREATED, event.getEventType());
        assertEquals("1", event.getJwk().getKid());
    }
}
