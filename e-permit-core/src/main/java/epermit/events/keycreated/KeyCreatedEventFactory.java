package epermit.events.keycreated;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import epermit.common.EventType;
import epermit.entities.Key;
import epermit.events.EventFactoryUtil;

public class KeyCreatedEventFactory {
    private final EventFactoryUtil util;

    public KeyCreatedEventFactory(EventFactoryUtil util) {
        this.util = util;
    }

    public KeyCreatedEvent create(Key key, String issuedFor) {
        KeyCreatedEvent e = KeyCreatedEvent.builder().keyId(key.getKeyId())
                .validFrom(OffsetDateTime.now(ZoneOffset.UTC).toEpochSecond()).jwk(key.getPublicJwk())
                .build();
        e.setEventType(EventType.KEY_CREATED);
        util.setCommon(e, issuedFor);
        return e;
    }
}
