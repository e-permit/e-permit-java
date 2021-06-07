package epermit.services;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import epermit.entities.Authority;
import epermit.entities.ReceivedEvent;
import epermit.events.EventHandler;
import epermit.events.EventType;
import epermit.events.EventValidationResult;
import epermit.events.EventValidator;
import epermit.events.ReceivedAppEvent;
import epermit.events.keycreated.KeyCreatedEvent;
import epermit.events.keycreated.KeyCreatedEventHandler;
import epermit.events.keycreated.KeyCreatedEventValidator;
import epermit.models.EPermitProperties;
import epermit.models.dtos.PublicJwk;
import epermit.models.results.JwsValidationResult;
import epermit.repositories.AuthorityRepository;
import epermit.repositories.ReceivedEventRepository;
import epermit.utils.GsonUtil;
import epermit.utils.JwsUtil;

@ExtendWith(MockitoExtension.class)
public class ReceivedEventServiceTest {
        @Mock
        private JwsUtil jwsUtil;

        @Mock
        private ReceivedEventRepository receivedEventRepository;

        @Spy
        private Map<String, EventHandler> eventHandlers = new HashMap<>();

        @Spy
        private Map<String, EventValidator> eventValidators = new HashMap<>();

        @Mock
        private RestTemplate restTemplate;

        @Mock
        private AuthorityRepository authorityRepository;

        @Mock
        private EPermitProperties properties;

        @Mock
        KeyCreatedEventHandler keyCreatedEventHandler;

        @Mock
        KeyCreatedEventValidator keyCreatedEventValidator;

        @InjectMocks
        private ReceivedEventService eventService;

        @Test
        void handleOkTest() {
                Map<String, Object> claims = Map.of("issuer", "TR", "issued_for", "UA",
                                "event_type", EventType.KEY_CREATED, "event_id", "1",
                                "previous_event_id", "0", "key_id", "1");

                when(receivedEventRepository.existsByIssuerAndEventId("TR", "1")).thenReturn(false);
                when(receivedEventRepository.existsByIssuerAndEventId("TR", "0")).thenReturn(true);
                when(keyCreatedEventValidator.validate(claims)).thenReturn(EventValidationResult
                                .success(GsonUtil.fromMap(claims, KeyCreatedEvent.class)));
                eventHandlers.put("KEY_CREATED_EVENT_HANDLER", keyCreatedEventHandler);
                eventValidators.put("KEY_CREATED_EVENT_VALIDATOR", keyCreatedEventValidator);
                ReceivedEventService receivedEventService = new ReceivedEventService(jwsUtil,
                                receivedEventRepository, eventHandlers, eventValidators,
                                restTemplate, authorityRepository, properties);
                EventValidationResult r = receivedEventService.handle(claims);
                assertTrue(r.isOk());
                verify(keyCreatedEventHandler, times(1))
                                .handle(GsonUtil.fromMap(claims, KeyCreatedEvent.class));
        }

        @Test
        void handleEventExistTest() {
                Map<String, Object> claims = Map.of("issuer", "TR", "issued_for", "UA",
                                "event_type", EventType.KEY_CREATED, "event_id", "1",
                                "previous_event_id", "0", "key_id", "1");

                when(receivedEventRepository.existsByIssuerAndEventId("TR", "1")).thenReturn(true);

                EventValidationResult r = eventService.handle(claims);
                assertFalse(r.isOk());
                assertEquals("EVENT_EXIST", r.getErrorCode());
                verify(keyCreatedEventHandler, never()).handle(any());
        }

        @Test
        void handleNotExistPreviousEventTest() {
                Map<String, Object> claims = Map.of("issuer", "TR", "issued_for", "UA",
                                "event_type", EventType.KEY_CREATED, "event_id", "1",
                                "previous_event_id", "0", "key_id", "1");

                when(receivedEventRepository.existsByIssuer("TR")).thenReturn(true);
                when(receivedEventRepository.existsByIssuerAndEventId("TR", "1")).thenReturn(false);
                when(receivedEventRepository.existsByIssuerAndEventId("TR", "0")).thenReturn(false);
                EventValidationResult r = eventService.handle(claims);
                assertFalse(r.isOk());
                assertEquals("NOTEXIST_PREVIOUSEVENT", r.getErrorCode());
                verify(keyCreatedEventHandler, never()).handle(any());
        }

        @Test
        void handleNotImplementedValidatorTest() {
                Map<String, Object> claims = Map.of("issuer", "TR", "issued_for", "UA",
                                "event_type", EventType.KEY_CREATED, "event_id", "1",
                                "previous_event_id", "0", "key_id", "1");

                when(receivedEventRepository.existsByIssuerAndEventId("TR", "1")).thenReturn(false);
                when(receivedEventRepository.existsByIssuerAndEventId("TR", "0")).thenReturn(true);
                assertThrows(Exception.class, () -> {
                        eventService.handle(claims);
                });
                verify(keyCreatedEventHandler, never()).handle(any());
        }

        @Test
        void handleValidateIsNotOkTest() {
                Map<String, Object> claims = Map.of("issuer", "TR", "issued_for", "UA",
                                "event_type", EventType.KEY_CREATED, "event_id", "1",
                                "previous_event_id", "0", "key_id", "1");

                when(receivedEventRepository.existsByIssuerAndEventId("TR", "1")).thenReturn(false);
                when(receivedEventRepository.existsByIssuerAndEventId("TR", "0")).thenReturn(true);
                when(keyCreatedEventValidator.validate(claims)).thenReturn(EventValidationResult
                                .fail("ERROR", GsonUtil.fromMap(claims, KeyCreatedEvent.class)));
                eventValidators.put("KEY_CREATED_EVENT_VALIDATOR", keyCreatedEventValidator);
                ReceivedEventService receivedEventService = new ReceivedEventService(jwsUtil,
                                receivedEventRepository, eventHandlers, eventValidators,
                                restTemplate, authorityRepository, properties);
                EventValidationResult r = receivedEventService.handle(claims);
                assertFalse(r.isOk());
                assertEquals("ERROR", r.getErrorCode());
                verify(keyCreatedEventHandler, never()).handle(any());
        }

        @Test
        void handleNotImplementedHandlerTest() {
                Map<String, Object> claims = Map.of("issuer", "TR", "issued_for", "UA",
                                "event_type", EventType.KEY_CREATED, "event_id", "1",
                                "previous_event_id", "0", "key_id", "1");

                when(receivedEventRepository.existsByIssuerAndEventId("TR", "1")).thenReturn(false);
                when(receivedEventRepository.existsByIssuerAndEventId("TR", "0")).thenReturn(true);
                when(keyCreatedEventValidator.validate(claims)).thenReturn(EventValidationResult
                                .success(GsonUtil.fromMap(claims, KeyCreatedEvent.class)));
                eventValidators.put("KEY_CREATED_EVENT_VALIDATOR", keyCreatedEventValidator);
                ReceivedEventService receivedEventService = new ReceivedEventService(jwsUtil,
                                receivedEventRepository, eventHandlers, eventValidators,
                                restTemplate, authorityRepository, properties);
                assertThrows(Exception.class, () -> {
                        receivedEventService.handle(claims);
                });
                verify(keyCreatedEventHandler, never()).handle(any());
        }

        @Test
        void handleReceivedEventOkTest() {
                ReceivedEventService eventService2 = spy(eventService);
                doReturn(EventValidationResult.success(null)).when(eventService2).handle(anyMap());
                ReceivedAppEvent event = new ReceivedAppEvent();
                event.setClaims(Map.of());
                eventService2.handleReceivedEvent(event);
                verify(eventService2, times(1)).handle(anyMap());
        }

        @Test
        void handleReceivedEventErrorTest() {
                ReceivedEventService eventService2 = spy(eventService);
                doReturn(EventValidationResult.fail("NOTEXIST_PREVIOUSEVENT_", null))
                                .when(eventService2).handle(anyMap());
                ReceivedAppEvent event = new ReceivedAppEvent();
                event.setClaims(Map.of());
                eventService2.handleReceivedEvent(event);
                verify(eventService2, times(1)).handle(anyMap());
        }

        @Test
        void handleReceivedEventPreviousEventNotExistTest() {
                ReceivedEventService eventService2 = spy(eventService);
                KeyCreatedEvent keyCreatedEvent = new KeyCreatedEvent();
                keyCreatedEvent.setEventId("3");
                keyCreatedEvent.setEventType(EventType.KEY_CREATED);
                keyCreatedEvent.setIssuedFor("UZ");
                keyCreatedEvent.setIssuer("TR");
                keyCreatedEvent.setJwk(new PublicJwk());
                keyCreatedEvent.setPreviousEventId("2");
                doReturn(EventValidationResult.fail("NOTEXIST_PREVIOUSEVENT", keyCreatedEvent))
                                .when(eventService2).handle(Map.of("previous_event_id", "2"));
                doReturn(EventValidationResult.success(keyCreatedEvent)).when(eventService2)
                                .handle(Map.of());

                ReceivedEvent receivedEvent = new ReceivedEvent();
                receivedEvent.setEventId("1");
                when(receivedEventRepository.findTopByIssuerOrderByIdDesc("TR"))
                                .thenReturn(Optional.of(receivedEvent));
                Authority authority = new Authority();
                authority.setApiUri("apiUri");
                when(authorityRepository.findOneByCode("TR")).thenReturn(authority);
                Map<String, String> claims = new HashMap<>();
                claims.put("last_event_id", "1");
                claims.put("issuer", "UZ");
                claims.put("issued_for", "TR");
                when(restTemplate.exchange(ArgumentMatchers.anyString(),
                                ArgumentMatchers.eq(HttpMethod.GET), ArgumentMatchers.any(),
                                ArgumentMatchers.<Class<String[]>>any())).thenReturn(
                                                ResponseEntity.ok(new String[] {"abc"}));
                when(jwsUtil.validateJws(anyString())).thenReturn(JwsValidationResult.success(Map.of()));
                when(properties.getIssuerCode()).thenReturn("UZ");
                ReceivedAppEvent event = new ReceivedAppEvent();
                event.setClaims(Map.of("previous_event_id", "2"));
                eventService2.handleReceivedEvent(event);
                verify(eventService2, times(2)).handle(anyMap());
        }
}


/*
 * KeyCreatedEvent keyCreatedEvent = new KeyCreatedEvent(); keyCreatedEvent.setEventId("1");
 * keyCreatedEvent.setEventType(EventType.KEY_CREATED); keyCreatedEvent.setIssuedFor("UZ");
 * keyCreatedEvent.setIssuer("TR"); keyCreatedEvent.setJwk("jwk"); keyCreatedEvent.setKeyId("1");
 * keyCreatedEvent.setPreviousEventId("0");
 * keyCreatedEvent.setValidFrom(OffsetDateTime.now(ZoneOffset.UTC).toEpochSecond());
 */
