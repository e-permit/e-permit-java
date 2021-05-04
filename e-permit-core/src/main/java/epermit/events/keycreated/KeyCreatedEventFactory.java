package epermit.events.keycreated;

import epermit.common.EventType;
import epermit.entities.Key;

public class KeyCreatedEventFactory {
    public KeyCreatedEvent create(Key key) {
        KeyCreatedEvent e = KeyCreatedEvent.builder().keyId(key.getKid()).jwk(key.getContent()).build();
        e.setEventType(EventType.KEY_CREATED);
        return e;
    }
}