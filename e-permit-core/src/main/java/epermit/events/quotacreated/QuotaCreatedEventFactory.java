package epermit.events.quotacreated;

import epermit.common.EventType;
import epermit.entities.VerifierQuota;
import epermit.events.EventFactoryUtil;

public class QuotaCreatedEventFactory {
    private final EventFactoryUtil util;

    public QuotaCreatedEventFactory(EventFactoryUtil util) {
        this.util = util;
    }

    public QuotaCreatedEvent create(VerifierQuota quota) {
        QuotaCreatedEvent e = QuotaCreatedEvent.builder().endNumber(quota.getEndNumber())
                .permitType(quota.getPermitType()).permitYear(quota.getPermitYear())
                .startNumber(quota.getStartNumber()).build();
        e.setEventType(EventType.QUOTA_CREATED);
        util.setCommon(e, quota.getAuthority().getCode());
        return e;
    }
}
