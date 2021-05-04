package epermit.events.quotacreated;

import epermit.common.EventType;
import epermit.entities.VerifierQuota;

public class QuotaCreatedEventFactory {
    public QuotaCreatedEvent create(VerifierQuota quota) {
        QuotaCreatedEvent e = QuotaCreatedEvent.builder().endId(quota.getEndNumber()).permitType(quota.getPermitType())
                .permitYear(quota.getPermitYear()).startId(quota.getStartNumber()).build();
        e.setEventType(EventType.QUOTA_CREATED);
        return e;
    }
}
