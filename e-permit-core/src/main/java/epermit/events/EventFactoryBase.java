package epermit.events;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;
import epermit.common.EventType;
import epermit.common.PermitProperties;
import epermit.entities.CreatedEvent;
import epermit.repositories.CreatedEventRepository;
import epermit.services.KeyService;

public abstract class EventFactoryBase {
    private PermitProperties props;
    private CreatedEventRepository createdEventRepository;
    private KeyService keyService;

    public EventFactoryBase(PermitProperties props,
    CreatedEventRepository createdEventRepository, KeyService keyService) {
        this.keyService = keyService;
        this.createdEventRepository = createdEventRepository;
        this.props = props;
    }

    protected <T extends EventBase> CreatedEvent persist(T event) {
        CreatedEvent entity = new CreatedEvent();
        entity.setIssuedFor(event.getIssuedFor());
        entity.setContent(keyService.createJws(event));
        createdEventRepository.save(entity);
        return entity;
    }

    protected <T extends EventBase> void setCommonClaims(T event, String issuedFor,
            EventType type) {
        event.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC).toEpochSecond());
        event.setIssuer(props.getIssuerCode());
        event.setIssuedFor(issuedFor);
        event.setEventType(type);
        event.setEventId(UUID.randomUUID().toString());
    }
}
