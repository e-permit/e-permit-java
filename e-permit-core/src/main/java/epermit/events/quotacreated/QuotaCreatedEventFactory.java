package epermit.events.quotacreated;

import epermit.common.EventType;
import epermit.common.PermitProperties;
import epermit.entities.CreatedEvent;
import epermit.entities.VerifierQuota;
import epermit.events.EventFactoryBase;
import epermit.repositories.CreatedEventRepository;
import epermit.services.KeyService;

public class QuotaCreatedEventFactory extends EventFactoryBase {
    public QuotaCreatedEventFactory(PermitProperties props,
            CreatedEventRepository createdEventRepository, KeyService sJwsService) {
        super(props, createdEventRepository, sJwsService);
    }

    public CreatedEvent create(VerifierQuota quota) {
        QuotaCreatedEvent e = QuotaCreatedEvent.builder().endId(quota.getEndNumber())
                .permitType(quota.getPermitType()).permitYear(quota.getPermitYear())
                .startId(quota.getStartNumber()).build();
        setCommonClaims(e, quota.getAuthority().getCode(), EventType.QUOTA_CREATED);
        return persist(e);
    }
}
