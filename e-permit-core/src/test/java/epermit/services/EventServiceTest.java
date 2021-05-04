package epermit.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import epermit.common.EventType;
import epermit.common.PermitProperties;
import epermit.entities.Authority;
import epermit.entities.AuthorityKey;
import epermit.entities.Key;
import epermit.entities.ReceivedEvent;
import epermit.events.EventHandleResult;
import epermit.events.EventHandler;
import epermit.repositories.AuthorityRepository;
import epermit.repositories.KeyRepository;
import epermit.repositories.ReceivedEventRepository;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@ExtendWith(MockitoExtension.class)
@Slf4j
public class EventServiceTest {
    @Mock AuthorityRepository authorityRepository;
    @Mock ReceivedEventRepository receivedEventRepository;
    @Mock PermitProperties props;
    @Mock KeyService keyService;
    @Mock KeyRepository keyRepository;

    @Test
    @SneakyThrows
    void handle() {
        when(props.getKeyPassword()).thenReturn("123456");
        when(props.getIssuerCode()).thenReturn("UA");
        KeyService keyService = new KeyService(props, keyRepository, authorityRepository); 
        Key k = keyService.create("1");
        when(keyRepository.findOneByEnabledTrue()).thenReturn(Optional.of(k));
        Authority authority = new Authority();
        AuthorityKey authorityKey = new AuthorityKey();
        authorityKey.setJwk(keyService.getKey().toPublicJWK().toJSONString());
        authorityKey.setKid("1");
        authority.addKey(authorityKey);
        when(authorityRepository.findByCode(anyString())).thenReturn(Optional.of(authority));
        ReceivedEvent event = new ReceivedEvent();
        event.setEventId("0");
        event.setPreviousEventId("0");
        event.setEventType(EventType.KEY_CREATED);
        when(receivedEventRepository.findOneByEventId("1")).thenReturn(Optional.empty());
        when(receivedEventRepository.findOneByEventId("0")).thenReturn(Optional.of(event));
        Map<String, String> claims = new HashMap<>();
        claims.put("event_type", "KEY_CREATED");
        claims.put("event_id", "1");
        claims.put("previous_event_id", "0");
        claims.put("issuer", "TR");
        claims.put("issued_for", "UA");
        String jws = keyService.createJws(claims); 
        Map<String, EventHandler> eventHandlers = new HashMap<>();
        eventHandlers.put("KEY_CREATED", new KeyCreatedEventHandler());
        log.info("Event handler size: " + eventHandlers.size());
        EventService service = new EventService(receivedEventRepository, keyService, eventHandlers);
        EventHandleResult r = service.handle(jws);
        Assertions.assertTrue(r.isSucceed());
    }

    class KeyCreatedEventHandler implements EventHandler{

        @Override
        public EventHandleResult handle(String jws) {
            return EventHandleResult.success();
        }

    }
    
}
