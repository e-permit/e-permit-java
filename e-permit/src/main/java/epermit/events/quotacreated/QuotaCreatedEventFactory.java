package epermit.events.quotacreated;

import org.springframework.stereotype.Component;
import epermit.entities.VerifierQuota;
import epermit.events.EventFactoryUtil;
import epermit.events.EventType;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class QuotaCreatedEventFactory {
    private final EventFactoryUtil util;

    public QuotaCreatedEvent create(VerifierQuota quota) {
        QuotaCreatedEvent e = new QuotaCreatedEvent();
        e.setEndNumber(quota.getEndNumber());
        e.setPermitType(quota.getPermitType());
        e.setPermitYear(quota.getPermitYear());
        e.setStartNumber(quota.getStartNumber());
        e.setEventType(EventType.QUOTA_CREATED);
        util.saveAndPublish(e, quota.getAuthority().getCode());
        return e;
    }
}
