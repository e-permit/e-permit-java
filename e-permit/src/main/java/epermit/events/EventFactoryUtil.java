package epermit.events;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import epermit.entities.CreatedEvent;
import epermit.models.EPermitProperties;
import epermit.repositories.AuthorityRepository;
import epermit.repositories.CreatedEventRepository;
import epermit.utils.JwsUtil;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EventFactoryUtil {
    private final CreatedEventRepository createdEventRepository;
    private final EPermitProperties properties;
    private final ApplicationEventPublisher eventPublisher;
    private final JwsUtil jwsUtil;
    private final AuthorityRepository authorityRepository;

    /*public <T extends EventBase> void setCommon(T event, String issuedFor) {
        CreatedEvent lastEvent =
                createdEventRepository.findTopByIssuedForOrderByIdDesc(issuedFor).get();
        event.setPreviousEventId(lastEvent.getEventId());
        event.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC).toEpochSecond());
        event.setIssuer(properties.getIssuerCode());
        event.setIssuedFor(issuedFor);
        event.setEventId(UUID.randomUUID().toString());
    }*/

    public <T extends EventBase> void saveAndPublish(T event, String issuedFor) {
        CreatedEvent lastEvent =
                createdEventRepository.findTopByIssuedForOrderByIdDesc(issuedFor).get();
        event.setPreviousEventId(lastEvent.getEventId());
        event.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC).toEpochSecond());
        event.setIssuer(properties.getIssuerCode());
        event.setIssuedFor(issuedFor);
        event.setEventId(UUID.randomUUID().toString());
        String jws = jwsUtil.createJws(event);
        CreatedEvent createdEvent = new CreatedEvent();
        createdEvent.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC));
        createdEvent.setIssuedFor(issuedFor);
        createdEvent.setEventId(event.getEventId());
        createdEvent.setPreviousEventId(lastEvent.getEventId());
        createdEvent.setEventType(event.getEventType());
        createdEvent.setJws(jws);
        createdEventRepository.save(createdEvent);
        String apiUri = authorityRepository.findOneByCode(issuedFor).get().getApiUri();
        CreatedAppEvent appEvent = new CreatedAppEvent();
        appEvent.setJws(jws);
        appEvent.setUri(apiUri);
        eventPublisher.publishEvent(appEvent);
    }

}
