package epermit.events.permitrevoked;

import org.springframework.stereotype.Component;
import epermit.events.EventFactoryUtil;
import epermit.events.EventType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class PermitRevokedEventFactory {
    private final EventFactoryUtil util;
    public PermitRevokedEvent create(String issuedFor, String permitId) {
        log.info("PermitRevokedEventFactory started with issued_for: {}, permit_id: {}", issuedFor, permitId);
        PermitRevokedEvent e = new PermitRevokedEvent();
        e.setPermitId(permitId);
        e.setEventType(EventType.PERMIT_REVOKED);
        log.info("PermitRevokedEventFactory ended with {}", e);
        util.saveAndPublish(e, issuedFor);
        return e;
    }
}
