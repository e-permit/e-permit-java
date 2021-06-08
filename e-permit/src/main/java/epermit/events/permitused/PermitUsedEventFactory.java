package epermit.events.permitused;

import org.springframework.stereotype.Component;
import epermit.entities.PermitActivity;
import epermit.events.EventFactoryUtil;
import epermit.events.EventType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class PermitUsedEventFactory {
    private final EventFactoryUtil util;

    public PermitUsedEvent create(PermitActivity permitActivity) {
        log.info("PermitUsedEventFactory started with {}", permitActivity);
        PermitUsedEvent e = new PermitUsedEvent();
        e.setActivityType(permitActivity.getActivityType());
        e.setPermitId(permitActivity.getPermit().getPermitId());
        e.setActivityTimestamp(permitActivity.getActivityTimestamp());
        e.setEventType(EventType.PERMIT_USED);
        log.info("PermitUsedEventFactory ended with {}", e);
        util.saveAndPublish(e, permitActivity.getPermit().getIssuer());
        return e;
    }
}
