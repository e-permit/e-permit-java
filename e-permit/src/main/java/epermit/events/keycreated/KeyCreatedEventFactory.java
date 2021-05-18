package epermit.events.keycreated;

import org.springframework.stereotype.Component;
import epermit.entities.Key;
import epermit.events.EventFactoryUtil;
import epermit.events.EventType;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class KeyCreatedEventFactory {
    private final EventFactoryUtil util;

    public KeyCreatedEvent create(Key key, String issuedFor) {
        KeyCreatedEvent e = new KeyCreatedEvent();
        e.setKeyId(key.getKeyId());
        e.setValidFrom(key.getValidFrom());
        e.setEventType(EventType.KEY_CREATED);
        e.setJwk(key.getPublicJwk());
        util.saveAndPublish(e, issuedFor);
        return e;
    }
}
