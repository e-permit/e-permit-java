package epermit.events.permitrevoked;

import epermit.common.EventType;

public class PermitRevokedEventFactory {

    public PermitRevokedEvent create(String permitId) {
        PermitRevokedEvent e = PermitRevokedEvent.builder().permitId(permitId).build();
        e.setEventType(EventType.PERMIT_REVOKED);
        return e;
    }

}
