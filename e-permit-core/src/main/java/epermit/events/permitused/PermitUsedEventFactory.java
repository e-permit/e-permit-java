package epermit.events.permitused;

import epermit.common.EventType;
import epermit.common.PermitActivityType;
import epermit.common.PermitProperties;
import epermit.entities.CreatedEvent;
import epermit.events.EventFactoryBase;
import epermit.repositories.CreatedEventRepository;
import epermit.services.KeyService;

public class PermitUsedEventFactory extends EventFactoryBase {

    public PermitUsedEventFactory(PermitProperties props, CreatedEventRepository createdEventRepository,
            KeyService jwsService) {
        super(props, createdEventRepository, jwsService);
    }

    public CreatedEvent create(String issuedFor, String permitId, PermitActivityType activityType) {
        PermitUsedEvent e = PermitUsedEvent.builder().activityType(activityType).permitId(permitId).build();
        setCommonClaims(e, issuedFor, EventType.PERMIT_USED);
        return persist(e);
    }
}