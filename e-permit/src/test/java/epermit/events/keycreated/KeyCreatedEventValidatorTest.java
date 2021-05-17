package epermit.events.keycreated;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import epermit.events.EventValidationResult;
import epermit.repositories.AuthorityKeyRepository;
import epermit.repositories.AuthorityRepository;
import epermit.utils.GsonUtil;

@ExtendWith(MockitoExtension.class)

public class KeyCreatedEventValidatorTest {
    @Mock
    AuthorityRepository authorityRepository;
    
    @Mock
    AuthorityKeyRepository authorityKeyRepository;

    @InjectMocks
    KeyCreatedEventValidator validator;

    @Test
    void okTest() {
        String jwk =
                "{\"kty\":\"EC\",\"crv\":\"P-256\",\"x\":\"b-twdhMdnpLQJ_pQx8meWsvevCyD0sufkdgF9nIsX-U\",\"y\":\"U339OypYc4efK_xKJqnGSgWbLQ--47sCfpu-pJU2620\",\"use\":\"sig\",\"kid\":\"1\",\"alg\":\"ES256\"}";
        Long utc = OffsetDateTime.now(ZoneOffset.UTC).toEpochSecond();
        KeyCreatedEvent event = new KeyCreatedEvent();
        event.setKeyId("1");
        event.setJwk(jwk);
        event.setValidFrom(utc);
        event.setIssuer("UA");
        String payload = GsonUtil.getGson().toJson(event);
        EventValidationResult r = validator.validate(payload);
        assertTrue(r.isOk());
    }

    @Test
    void invalidKeyIdTest() {
        String jwk =
                "{\"kty\":\"EC\",\"crv\":\"P-256\",\"x\":\"b-twdhMdnpLQJ_pQx8meWsvevCyD0sufkdgF9nIsX-U\",\"y\":\"U339OypYc4efK_xKJqnGSgWbLQ--47sCfpu-pJU2620\",\"use\":\"sig\",\"kid\":\"2\",\"alg\":\"ES256\"}";
        Long utc = OffsetDateTime.now(ZoneOffset.UTC).toEpochSecond();
        KeyCreatedEvent event = new KeyCreatedEvent();
        event.setKeyId("1");
        event.setJwk(jwk);
        event.setValidFrom(utc);
        event.setIssuer("UA");
        String payload = GsonUtil.getGson().toJson(event);
        EventValidationResult r = validator.validate(payload);
        assertFalse(r.isOk());
        assertEquals("INVALID_KID", r.getErrorCode());
    }

    @Test
    void invalidJwkTest() {
        String jwk =
                "{\"kty\":\"EC\",\"crv\":\"P-256\",\"x\":\"\",\"y\":\"U339OypYc4efK_xKJqnGSgWbLQ--47sCfpu-pJU2620\",\"use\":\"sig\",\"kid\":\"1\",\"alg\":\"ES256\"}";
        Long utc = OffsetDateTime.now(ZoneOffset.UTC).toEpochSecond();
        KeyCreatedEvent event = new KeyCreatedEvent();
        event.setKeyId("1");
        event.setJwk(jwk);
        event.setValidFrom(utc);
        event.setIssuer("UA");
        String payload = GsonUtil.getGson().toJson(event);
        EventValidationResult r = validator.validate(payload);
        assertFalse(r.isOk());
        assertEquals("INVALID_KEY", r.getErrorCode());
    }
}
