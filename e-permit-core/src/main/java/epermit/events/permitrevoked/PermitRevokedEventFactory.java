package epermit.events.permitrevoked;

import epermit.common.EventType;
import epermit.common.PermitProperties;
import epermit.entities.CreatedEvent;
import epermit.events.EventFactoryBase;
import epermit.repositories.CreatedEventRepository;
import epermit.services.KeyService;

public class PermitRevokedEventFactory extends EventFactoryBase {

    public PermitRevokedEventFactory(PermitProperties props, CreatedEventRepository createdEventRepository,
            KeyService jwsService) {
        super(props, createdEventRepository, jwsService);
    }

    public CreatedEvent create(String issuedFor, String permitId) {
        PermitRevokedEvent e = PermitRevokedEvent.builder().permitId(permitId).build();
        setCommonClaims(e, issuedFor, EventType.PERMIT_REVOKED);
        return persist(e);
    }

}
