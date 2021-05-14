package epermit.events.permitrevoked;

import org.springframework.stereotype.Component;
import epermit.events.EventFactoryUtil;
import epermit.events.EventType;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PermitRevokedEventFactory {
    private final EventFactoryUtil util;
    public PermitRevokedEvent create(String issuedFor, String permitId) {
        PermitRevokedEvent e = new PermitRevokedEvent();
        e.setPermitId(permitId);
        e.setEventType(EventType.PERMIT_REVOKED);
        util.setCommon(e, issuedFor);
        return e;
    }
}
