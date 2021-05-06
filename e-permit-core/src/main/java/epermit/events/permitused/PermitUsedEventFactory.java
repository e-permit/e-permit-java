package epermit.events.permitused;

import epermit.common.EventType;
import epermit.common.PermitActivityType;
import epermit.entities.Permit;
import epermit.events.EventFactoryUtil;

public class PermitUsedEventFactory {
    private final EventFactoryUtil util;

    public PermitUsedEventFactory(EventFactoryUtil util) {
        this.util = util;
    }

    public PermitUsedEvent create(Permit permit, PermitActivityType activityType) {
        PermitUsedEvent e = PermitUsedEvent.builder().activityType(activityType)
                .permitId(permit.getPermitId()).build();
        e.setEventType(EventType.PERMIT_USED);
        util.setCommon(e, permit.getIssuer());
        return e;
    }
}
