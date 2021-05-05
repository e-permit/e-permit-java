package epermit.events;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;
import epermit.common.PermitProperties;
import epermit.entities.CreatedEvent;
import epermit.repositories.CreatedEventRepository;

public class EventFactoryUtil {
    private final PermitProperties properties;
    private final CreatedEventRepository createdEventRepository;

    public EventFactoryUtil(PermitProperties properties,
            CreatedEventRepository createdEventRepository) {
        this.properties = properties;
        this.createdEventRepository = createdEventRepository;
    }

    public <T extends EventBase> void setCommon(T event, String issuedFor) {
        CreatedEvent lastEvent = createdEventRepository.findTopByIssuedForOrderByIdDesc(issuedFor);
        event.setPreviousEventId(lastEvent.getEventId());
        event.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC).toEpochSecond());
        event.setIssuer(properties.getIssuerCode());
        event.setIssuedFor(issuedFor);
        event.setEventId(UUID.randomUUID().toString());
    }

}
