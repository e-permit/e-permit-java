package epermit.services;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;
import epermit.common.JwsValidationResult;
import epermit.common.PermitProperties;
import epermit.entities.ReceivedEvent;
import epermit.events.EventHandleResult;
import epermit.events.EventHandler;
import epermit.repositories.AuthorityRepository;
import epermit.repositories.CreatedEventRepository;
import epermit.repositories.KeyRepository;
import epermit.repositories.ReceivedEventRepository;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@ExtendWith(MockitoExtension.class)
@Slf4j
public class EventServiceTest {
    final String jws =
            "eyJraWQiOiIxIiwiYWxnIjoiRVMyNTYifQ.eyJpc3N1ZWRfZm9yIjoiVUEiLCJldmVudF90eXBlIjoiS0VZX0NSRUFURUQiLCJldmVudF9pZCI6IjEiLCJwcmV2aW91c19ldmVudF9pZCI6IjAiLCJpc3N1ZXIiOiJUUiJ9.CfPibn_S8nxIAdBpnkxgkV_bYtUMNX6wtp-Q6R6ZjZUyuqVPug4mKutwda8aEMIP-83q_Xoxd35CFlGYK0VAQw";
    @Mock
    AuthorityRepository authorityRepository;
    @Mock
    ReceivedEventRepository receivedEventRepository;
    @Mock
    PermitProperties props;
    @Mock
    KeyService keyService;
    @Mock
    KeyRepository keyRepository;
    @Mock
    CreatedEventRepository createdEventRepository;
    @Mock
    RestTemplate restTemplate;

    @Test
    @SneakyThrows
    void handleShouldWork() {
        /*
         * when(props.getKeyPassword()).thenReturn("123456");
         * when(props.getIssuerCode()).thenReturn("UA"); KeyService keyService = new
         * KeyService(props, keyRepository, authorityRepository); Key k = keyService.create("1");
         * when(keyRepository.findOneByEnabledTrue()).thenReturn(Optional.of(k)); Authority
         * authority = new Authority(); AuthorityKey authorityKey = new AuthorityKey();
         * authorityKey.setJwk(keyService.getKey().toPublicJWK().toJSONString());
         * authorityKey.setKid("1"); authority.addKey(authorityKey);
         * when(authorityRepository.findOneByCode(anyString())).thenReturn(Optional.of(authority));
         * ReceivedEvent event = new ReceivedEvent(); event.setEventId("0");
         * event.setPreviousEventId("0"); event.setEventType(EventType.KEY_CREATED);
         * when(receivedEventRepository.findOneByIssuerAndEventId("TR", "1"))
         * .thenReturn(Optional.empty());
         * when(receivedEventRepository.findOneByIssuerAndEventId("TR", "0"))
         * .thenReturn(Optional.of(event)); Map<String, String> claims = new HashMap<>();
         * claims.put("event_type", "KEY_CREATED"); claims.put("event_id", "1");
         * claims.put("previous_event_id", "0"); claims.put("issuer", "TR");
         * claims.put("issued_for", "UA"); String jws = keyService.createJws(claims);
         */
        when(keyService.validateJws(anyString())).thenReturn(JwsValidationResult.success());
        ReceivedEvent e = new ReceivedEvent();
        when(receivedEventRepository.findOneByIssuerAndEventId("TR", "1"))
                .thenReturn(Optional.empty());
        when(receivedEventRepository.findOneByIssuerAndEventId("TR", "0"))
                .thenReturn(Optional.of(e));
        Map<String, EventHandler> eventHandlers = new HashMap<>();
        eventHandlers.put("KEY_CREATED", new KeyCreatedEventHandler());
        log.info("Event handler size: " + eventHandlers.size());
        EventService service = new EventService(receivedEventRepository, keyService, eventHandlers,
                createdEventRepository, authorityRepository, restTemplate);
        EventHandleResult r = service.handle(jws);
        Assertions.assertTrue(r.isSucceed());
    }

    @Test
    @SneakyThrows
    void handleShouldReturnInvalidJws() {
        when(keyService.validateJws(anyString())).thenReturn(JwsValidationResult.fail("errorCode"));
        EventService service = new EventService(receivedEventRepository, keyService, null,
                createdEventRepository, authorityRepository, restTemplate);
        EventHandleResult r = service.handle("jws");
        Assertions.assertFalse(r.isSucceed());
        Assertions.assertEquals("errorCode", r.getErrorCode());
    }

    @Test
    @SneakyThrows
    void handleShouldReturnEventExist() {
        when(keyService.validateJws(anyString())).thenReturn(JwsValidationResult.success());
        ReceivedEvent e = new ReceivedEvent();
        when(receivedEventRepository.findOneByIssuerAndEventId("TR", "1"))
                .thenReturn(Optional.of(e));
        EventService service =
                new EventService(receivedEventRepository, keyService, null, null, null, null);
        EventHandleResult r = service.handle(jws);
        Assertions.assertFalse(r.isSucceed());
        Assertions.assertEquals("EXIST_EVENT", r.getErrorCode());
    }

    @Test
    @SneakyThrows
    void handleShouldReturnPreviousEventNotExist() {
        when(keyService.validateJws(anyString())).thenReturn(JwsValidationResult.success());
        when(receivedEventRepository.findOneByIssuerAndEventId("TR", "1"))
                .thenReturn(Optional.empty());
        when(receivedEventRepository.findOneByIssuerAndEventId("TR", "0"))
                .thenReturn(Optional.empty());
        EventService service = new EventService(receivedEventRepository, keyService, null, null, null, null);
        EventHandleResult r = service.handle(jws);
        Assertions.assertFalse(r.isSucceed());
        Assertions.assertEquals("NOTEXIST_PREVIOUSEVENT", r.getErrorCode());
    }

    class KeyCreatedEventHandler implements EventHandler {
        @Override
        public EventHandleResult handle(String jws) {
            return EventHandleResult.success();
        }
    }

}
