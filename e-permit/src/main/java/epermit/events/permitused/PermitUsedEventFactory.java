package epermit.events.permitused;

import java.time.Instant;
import org.springframework.stereotype.Component;
import epermit.entities.Permit;
import epermit.entities.PermitActivity;
import epermit.events.EventFactoryUtil;
import epermit.events.EventType;
import epermit.models.enums.PermitActivityType;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PermitUsedEventFactory {
    private final EventFactoryUtil util;

    public PermitUsedEvent create(PermitActivity permitActivity) {
        PermitUsedEvent e = new PermitUsedEvent();
        e.setActivityType(permitActivity.getActivityType());
        e.setPermitId(permitActivity.getPermit().getPermitId());
        e.setActivityTimestamp(permitActivity.getActivityTimestamp());
        e.setEventType(EventType.PERMIT_USED);
        util.saveAndPublish(e, permitActivity.getPermit().getIssuer());
        return e;
    }
}
