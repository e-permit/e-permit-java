package epermit.events.keycreated;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import epermit.common.JsonUtil;
import epermit.entities.Authority;
import epermit.entities.AuthorityKey;
import epermit.events.EventHandleResult;
import epermit.repositories.AuthorityRepository;

@ExtendWith(MockitoExtension.class)

public class KeyCreatedEventHandlerTest {
    @Mock
    AuthorityRepository repository;

    @Test
    void handleShouldWork() {
        String jwk =
                "{\"kty\":\"EC\",\"crv\":\"P-256\",\"x\":\"b-twdhMdnpLQJ_pQx8meWsvevCyD0sufkdgF9nIsX-U\",\"y\":\"U339OypYc4efK_xKJqnGSgWbLQ--47sCfpu-pJU2620\",\"use\":\"sig\",\"kid\":\"1\",\"alg\":\"ES256\"}";
        Long utc = OffsetDateTime.now(ZoneOffset.UTC).toEpochSecond();
        Authority authority = new Authority();
        AuthorityKey key = new AuthorityKey();
        key.setAuthority(authority);
        key.setKeyId("1");
        key.setJwk(jwk);
        key.setValidFrom(utc);
        when(repository.findOneByCode("UA")).thenReturn(Optional.of(authority));
        KeyCreatedEventHandler handler = new KeyCreatedEventHandler(repository);
        KeyCreatedEvent event =
                KeyCreatedEvent.builder().keyId("1").jwk(jwk).validFrom(utc).build();
        event.setIssuer("UA");
        String payload = JsonUtil.getGson().toJson(event);
        EventHandleResult r = handler.handle(payload);
        assertTrue(r.isSucceed());
    }

    @Test
    void returnInvalidJwkWhenKeyIdIsDifferent() {
        String jwk =
                "{\"kty\":\"EC\",\"crv\":\"P-256\",\"x\":\"b-twdhMdnpLQJ_pQx8meWsvevCyD0sufkdgF9nIsX-U\",\"y\":\"U339OypYc4efK_xKJqnGSgWbLQ--47sCfpu-pJU2620\",\"use\":\"sig\",\"kid\":\"2\",\"alg\":\"ES256\"}";
        Long utc = OffsetDateTime.now(ZoneOffset.UTC).toEpochSecond();

        KeyCreatedEventHandler handler = new KeyCreatedEventHandler(repository);
        KeyCreatedEvent event =
                KeyCreatedEvent.builder().keyId("1").jwk(jwk).validFrom(utc).build();
        EventHandleResult r = handler.handle(JsonUtil.getGson().toJson(event));
        assertFalse(r.isSucceed());
        assertEquals("INVALID_KID", r.getErrorCode());
    }

    @Test
    void returnInvalidJwkWhenKeyIsNotValid() {
        String jwk =
                "{\"kty\":\"EC\",\"crv\":\"P-256\",\"x\":\"\",\"y\":\"U339OypYc4efK_xKJqnGSgWbLQ--47sCfpu-pJU2620\",\"use\":\"sig\",\"kid\":\"1\",\"alg\":\"ES256\"}";
        Long utc = OffsetDateTime.now(ZoneOffset.UTC).toEpochSecond();

        KeyCreatedEventHandler handler = new KeyCreatedEventHandler(repository);
        KeyCreatedEvent event =
                KeyCreatedEvent.builder().keyId("1").jwk(jwk).validFrom(utc).build();
        EventHandleResult r = handler.handle(JsonUtil.getGson().toJson(event));
        assertFalse(r.isSucceed());
        assertEquals("INVALID_KEY", r.getErrorCode());
    }
}
