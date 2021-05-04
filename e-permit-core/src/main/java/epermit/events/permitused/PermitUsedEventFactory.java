package epermit.events.permitused;

import epermit.common.EventType;
import epermit.common.PermitActivityType;

public class PermitUsedEventFactory {
    public PermitUsedEvent create(String permitId, PermitActivityType activityType) {
        PermitUsedEvent e = PermitUsedEvent.builder().activityType(activityType).permitId(permitId).build();
        e.setEventType(EventType.PERMIT_USED);
        return e;
    }
}