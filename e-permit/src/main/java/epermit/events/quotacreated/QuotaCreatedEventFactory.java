package epermit.events.quotacreated;

import org.springframework.stereotype.Component;
import epermit.entities.VerifierQuota;
import epermit.events.EventFactoryUtil;
import epermit.events.EventType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class QuotaCreatedEventFactory {
    private final EventFactoryUtil util;

    public QuotaCreatedEvent create(VerifierQuota quota) {
        log.info("QuotaCreatedEventFactory started with {}", quota);
        QuotaCreatedEvent e = new QuotaCreatedEvent();
        e.setEndNumber(quota.getEndNumber());
        e.setPermitType(quota.getPermitType());
        e.setPermitYear(quota.getPermitYear());
        e.setStartNumber(quota.getStartNumber());
        e.setEventType(EventType.QUOTA_CREATED);
        String issuedFor = quota.getAuthority().getCode();
        log.info("QuotaCreatedEventFactory ended with {}", e);
        util.saveAndPublish(e, issuedFor);
        return e;
    }
}
