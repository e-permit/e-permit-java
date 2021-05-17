package epermit.events.keycreated;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.springframework.stereotype.Component;
import epermit.events.EventFactoryUtil;
import epermit.events.EventType;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class KeyCreatedEventFactory {
    private final EventFactoryUtil util;
    public KeyCreatedEvent create(String keyId, String issuedFor) {
        KeyCreatedEvent e = new KeyCreatedEvent();
        e.setKeyId(keyId);
        e.setValidFrom(OffsetDateTime.now(ZoneOffset.UTC).toEpochSecond());
        e.setEventType(EventType.KEY_CREATED);
        util.saveAndPublish(e, issuedFor);
        return e;
    }
}
