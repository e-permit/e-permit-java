package epermit.events.quotacreated;

import org.springframework.stereotype.Component;
import epermit.events.EventFactoryUtil;
import epermit.events.EventType;
import epermit.models.PermitType;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class QuotaCreatedEventFactory {
    private final EventFactoryUtil util;

    public QuotaCreatedEvent create(QuotaCreatedEventInput input) {
        QuotaCreatedEvent e = new QuotaCreatedEvent();
        e.setEndNumber(input.getEndNumber());
        e.setPermitType(input.getPermitType());
        e.setPermitYear(input.getPermitYear());
        e.setStartNumber(input.getStartNumber());
        e.setEventType(EventType.QUOTA_CREATED);
        util.saveAndPublish(e, input.getIssuedFor());
        return e;
    }
}
