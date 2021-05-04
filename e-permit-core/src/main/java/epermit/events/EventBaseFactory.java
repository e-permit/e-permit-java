package epermit.events;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import org.springframework.stereotype.Component;

import epermit.common.PermitProperties;
import epermit.entities.CreatedEvent;
import epermit.repositories.CreatedEventRepository;

@Component
public class EventBaseFactory {
    private PermitProperties props;
    private CreatedEventRepository createdEventRepository;

    public EventBaseFactory(PermitProperties props, CreatedEventRepository createdEventRepository) {
        this.createdEventRepository = createdEventRepository;
        this.props = props;
    }

    public <T extends EventBase> void setCommon(T event, String issuedFor) {
        CreatedEvent lastEvent = createdEventRepository.findTopByOrderByIdDesc();
        event.setPreviousEventId(lastEvent.getEventId());
        event.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC).toEpochSecond());
        event.setIssuer(props.getIssuerCode());
        event.setIssuedFor(issuedFor);
        event.setEventId(UUID.randomUUID().toString());
    }
}
