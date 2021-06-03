package epermit.events;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
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

    public <T extends EventBase> void saveAndPublish(T event, String issuedFor) {
        Optional<CreatedEvent> lastEventR =
                createdEventRepository.findTopByIssuedForOrderByIdDesc(issuedFor);

        event.setPreviousEventId(lastEventR.isPresent() ? lastEventR.get().getEventId() : "0");
        event.setCreatedAt(Instant.now().getEpochSecond());
        event.setIssuer(properties.getIssuerCode());
        event.setIssuedFor(issuedFor);
        event.setEventId(UUID.randomUUID().toString());
        String jws = jwsUtil.createJws(event);
        CreatedEvent createdEvent = new CreatedEvent();
        createdEvent.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));
        createdEvent.setIssuedFor(issuedFor);
        createdEvent.setEventId(event.getEventId());
        createdEvent.setPreviousEventId(event.getPreviousEventId());
        createdEvent.setEventType(event.getEventType());
        createdEvent.setJws(jws);
        createdEventRepository.save(createdEvent);
        String apiUri = authorityRepository.findOneByCode(issuedFor).getApiUri();
        CreatedAppEvent appEvent = new CreatedAppEvent();
        appEvent.setJws(jws);
        appEvent.setUri(apiUri);
        eventPublisher.publishEvent(appEvent);
    }

}
