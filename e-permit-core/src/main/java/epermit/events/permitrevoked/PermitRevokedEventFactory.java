package epermit.events.permitrevoked;

import epermit.common.EventType;
import epermit.entities.IssuedPermit;
import epermit.events.EventFactoryUtil;

public class PermitRevokedEventFactory {

    private final EventFactoryUtil util;

    public PermitRevokedEventFactory(EventFactoryUtil util) {
        this.util = util;
    }

    public PermitRevokedEvent create(IssuedPermit permit) {
        PermitRevokedEvent e = PermitRevokedEvent.builder().permitId(permit.getPermitId()).build();
        e.setEventType(EventType.PERMIT_REVOKED);
        util.setCommon(e, permit.getIssuedFor());
        return e;
    }

}
