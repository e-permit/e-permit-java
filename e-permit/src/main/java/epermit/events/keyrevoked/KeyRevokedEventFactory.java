package epermit.events.keyrevoked;

import java.time.Instant;
import org.springframework.stereotype.Component;
import epermit.events.EventFactoryUtil;
import epermit.events.EventType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class KeyRevokedEventFactory {
    private final EventFactoryUtil util;

    public KeyRevokedEvent create(String keyId, String issuedFor) {
        log.info("KeyRevokedEventFactory started with {}, {}", keyId, issuedFor);
        KeyRevokedEvent e = new KeyRevokedEvent();
        e.setKeyId(keyId);
        e.setRevokedAt(Instant.now().getEpochSecond());
        e.setEventType(EventType.KEY_REVOKED);
        log.info("KeyRevokedEventFactory ended with {}", e);
        util.saveAndPublish(e, issuedFor);
        return e;
    }
}
