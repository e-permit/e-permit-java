package epermit.events.permitused;

import org.springframework.stereotype.Component;
import epermit.events.EventFactoryUtil;
import epermit.events.EventType;
import epermit.models.PermitActivityType;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PermitUsedEventFactory {
    private final EventFactoryUtil util;

    public PermitUsedEvent create(String issuedFor, String permitId,
            PermitActivityType activityType) {
        PermitUsedEvent e = new PermitUsedEvent();
        e.setActivityType(activityType);
        e.setPermitId(permitId);
        e.setEventType(EventType.PERMIT_USED);
        util.setCommon(e, issuedFor);
        return e;
    }
}
