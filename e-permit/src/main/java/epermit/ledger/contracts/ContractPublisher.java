package epermit.ledger.contracts;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import epermit.ledger.utils.JwsUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ContractPublisher {
    private final EPermitProperties properties;
    private final ApplicationEventPublisher eventPublisher;
    private final JwsUtil jwsUtil;

    public <T extends ContractBase> void saveAndPublish(T contract, String issuedFor) {
        Optional<CreatedEvent> lastEventR =
                createdEventRepository.findTopByIssuedForOrderByIdDesc(issuedFor);
        event.setEventId(UUID.randomUUID().toString());
        if(lastEventR.isPresent()){
            event.setPreviousEventId(lastEventR.get().getEventId());
        }else{
            event.setPreviousEventId("0");
            log.info("First event created. Event id is {}", event.getEventId());
        }
        event.setCreatedAt(Instant.now().getEpochSecond());
        event.setIssuer(properties.getIssuerCode());
        event.setIssuedFor(issuedFor);
        
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
        appEvent.setUri(apiUri + "/events");
        eventPublisher.publishEvent(appEvent);
        log.info("Event published {}", appEvent);
    }

}

