package epermit.events.permitused;

import java.time.Instant;
import org.springframework.stereotype.Component;
import epermit.entities.Permit;
import epermit.events.EventFactoryUtil;
import epermit.events.EventType;
import epermit.models.enums.PermitActivityType;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PermitUsedEventFactory {
    private final EventFactoryUtil util;

    public PermitUsedEvent create(Permit permit, PermitActivityType activityType) {
        PermitUsedEvent e = new PermitUsedEvent();
        e.setActivityType(activityType);
        e.setPermitId(permit.getPermitId());
        e.setActivityTimestamp(Instant.now().getEpochSecond());
        e.setEventType(EventType.PERMIT_USED);
        util.saveAndPublish(e, permit.getIssuer());
        return e;
    }
}
