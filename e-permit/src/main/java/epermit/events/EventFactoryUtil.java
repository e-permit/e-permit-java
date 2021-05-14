package epermit.events;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;
import org.springframework.stereotype.Component;
import epermit.models.EPermitProperties;
import epermit.services.EventService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EventFactoryUtil {
    private final EventService eventService;
    private final EPermitProperties properties;

    public <T extends EventBase> void setCommon(T event, String issuedFor) {
        event.setPreviousEventId(eventService.getSendedLastEventId(issuedFor));
        event.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC).toEpochSecond());
        event.setIssuer(properties.getIssuerCode());
        event.setIssuedFor(issuedFor);
        event.setEventId(UUID.randomUUID().toString());
    }

}
