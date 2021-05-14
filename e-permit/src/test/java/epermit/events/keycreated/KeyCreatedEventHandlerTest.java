package epermit.events.keycreated;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import epermit.events.EventHandleResult;
import epermit.services.AuthorityService;
import epermit.utils.GsonUtil;

@ExtendWith(MockitoExtension.class)

public class KeyCreatedEventHandlerTest {
    @Mock
    AuthorityService authorityService;

    @InjectMocks
    KeyCreatedEventHandler handler;

    @Test
    void handleShouldWork() {
        String jwk =
                "{\"kty\":\"EC\",\"crv\":\"P-256\",\"x\":\"b-twdhMdnpLQJ_pQx8meWsvevCyD0sufkdgF9nIsX-U\",\"y\":\"U339OypYc4efK_xKJqnGSgWbLQ--47sCfpu-pJU2620\",\"use\":\"sig\",\"kid\":\"1\",\"alg\":\"ES256\"}";
        Long utc = OffsetDateTime.now(ZoneOffset.UTC).toEpochSecond();
        KeyCreatedEvent event =new KeyCreatedEvent();
        event.setKeyId("1");
        event.setJwk(jwk);
        event.setValidFrom(utc);
        event.setIssuer("UA");
        String payload = GsonUtil.getGson().toJson(event);
        EventHandleResult r = handler.handle(payload);
        assertTrue(r.isOk());
        verify(authorityService, times(1)).handleKeyCreated(event);
    }

    @Test
    void returnInvalidJwkWhenKeyIdIsDifferent() {
        String jwk =
                "{\"kty\":\"EC\",\"crv\":\"P-256\",\"x\":\"b-twdhMdnpLQJ_pQx8meWsvevCyD0sufkdgF9nIsX-U\",\"y\":\"U339OypYc4efK_xKJqnGSgWbLQ--47sCfpu-pJU2620\",\"use\":\"sig\",\"kid\":\"2\",\"alg\":\"ES256\"}";
        Long utc = OffsetDateTime.now(ZoneOffset.UTC).toEpochSecond();
        KeyCreatedEvent event =new KeyCreatedEvent();
        event.setKeyId("1");
        event.setJwk(jwk);
        event.setValidFrom(utc);
        event.setIssuer("UA");
        String payload = GsonUtil.getGson().toJson(event);
        EventHandleResult r = handler.handle(payload);
        assertFalse(r.isOk());
        assertEquals("INVALID_KID", r.getErrorCode());
        verify(authorityService, never()).handleKeyCreated(event);
    }

    @Test
    void returnInvalidJwkWhenKeyIsNotValid() {
        String jwk =
                "{\"kty\":\"EC\",\"crv\":\"P-256\",\"x\":\"\",\"y\":\"U339OypYc4efK_xKJqnGSgWbLQ--47sCfpu-pJU2620\",\"use\":\"sig\",\"kid\":\"1\",\"alg\":\"ES256\"}";
        Long utc = OffsetDateTime.now(ZoneOffset.UTC).toEpochSecond();
        KeyCreatedEvent event =new KeyCreatedEvent();
        event.setKeyId("1");
        event.setJwk(jwk);
        event.setValidFrom(utc);
        event.setIssuer("UA");
        String payload = GsonUtil.getGson().toJson(event);
        EventHandleResult r = handler.handle(payload);
        assertFalse(r.isOk());
        assertEquals("INVALID_KEY", r.getErrorCode());
        verify(authorityService, never()).handleKeyCreated(event);
    }
}
