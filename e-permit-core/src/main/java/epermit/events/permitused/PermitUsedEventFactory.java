package epermit.events.permitused;

import epermit.common.EventType;
import epermit.common.PermitProperties;
import epermit.entities.CreatedEvent;
import epermit.entities.Permit;
import epermit.events.EventFactoryBase;
import epermit.repositories.CreatedEventRepository;
import epermit.services.KeyService;

public class PermitUsedEventFactory extends EventFactoryBase {

    public PermitUsedEventFactory(PermitProperties props,
            CreatedEventRepository createdEventRepository, KeyService jwsService) {
        super(props, createdEventRepository, jwsService);
    }

    public CreatedEvent create(Permit permit) {
        PermitUsedEvent e =
                PermitUsedEvent.builder().serialNumber(permit.getSerialNumber()).build();
        setCommonClaims(e, permit.getIssuer(), EventType.PERMIT_USED);
        return persist(e);
    }
}