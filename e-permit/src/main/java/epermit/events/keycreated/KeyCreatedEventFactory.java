package epermit.events.keycreated;

import org.springframework.stereotype.Component;
import epermit.entities.Key;
import epermit.events.EventFactoryUtil;
import epermit.events.EventType;
import epermit.models.dtos.PublicJwk;
import epermit.utils.GsonUtil;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class KeyCreatedEventFactory {
    private final EventFactoryUtil util;

    public KeyCreatedEvent create(Key key, String issuedFor) {
        KeyCreatedEvent e = new KeyCreatedEvent();
        e.setEventType(EventType.KEY_CREATED);
        PublicJwk jwk = GsonUtil.getGson().fromJson(key.getPublicJwk(), PublicJwk.class);
        e.setJwk(jwk);
        util.saveAndPublish(e, issuedFor);
        return e;
    }
}
