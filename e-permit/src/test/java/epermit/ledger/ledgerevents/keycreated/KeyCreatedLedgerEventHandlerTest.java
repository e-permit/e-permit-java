package epermit.ledger.ledgerevents.keycreated;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import epermit.ledger.entities.Authority;
import epermit.ledger.models.dtos.PublicJwk;
import epermit.ledger.repositories.AuthorityRepository;

@ExtendWith(MockitoExtension.class)

public class KeyCreatedLedgerEventHandlerTest {
    @Mock
    AuthorityRepository authorityRepository;

    @InjectMocks
    KeyCreatedLedgerEventHandler handler;

    @Captor
    ArgumentCaptor<Authority> captor;

    @Test
    void okTest() {
        when(authorityRepository.findOneByCode("UA")).thenReturn(new Authority());
        KeyCreatedLedgerEvent event = new KeyCreatedLedgerEvent();
        PublicJwk jwk = new PublicJwk();
        jwk.setKid("1");
        /*event.setJwk(jwk);
        event.setIssuer("UA");
        handler.handle(event);
        verify(authorityRepository).save(captor.capture());
        AuthorityKey authorityKey = captor.getValue().getKeys().get(0);
        assertEquals("1", authorityKey.getKeyId());*/
    }

    /*@Test
    void okTest() {
        String jwk =
                "{\"kty\":\"EC\",\"crv\":\"P-256\",\"x\":\"b-twdhMdnpLQJ_pQx8meWsvevCyD0sufkdgF9nIsX-U\",\"y\":\"U339OypYc4efK_xKJqnGSgWbLQ--47sCfpu-pJU2620\",\"use\":\"sig\",\"kid\":\"1\",\"alg\":\"ES256\"}";
        KeyCreatedEvent event = new KeyCreatedEvent();
        event.setJwk(GsonUtil.getGson().fromJson(jwk, PublicJwk.class));
        event.setIssuer("UA");
        EventValidationResult r = validator.validate(GsonUtil.toMap(event));
        assertTrue(r.isOk());
    }

    @Test
    void invalidJwkTest() {
        String jwk =
                "{\"kty\":\"EC\",\"crv\":\"P-256\",\"x\":\"\",\"y\":\"U339OypYc4efK_xKJqnGSgWbLQ--47sCfpu-pJU2620\",\"use\":\"sig\",\"kid\":\"1\",\"alg\":\"ES256\"}";
        KeyCreatedEvent event = new KeyCreatedEvent();
        event.setJwk(GsonUtil.getGson().fromJson(jwk, PublicJwk.class));
        event.setIssuer("UA");
        EventValidationResult r = validator.validate(GsonUtil.toMap(event));
        assertFalse(r.isOk());
        assertEquals("INVALID_KEY", r.getErrorCode());
    }*/
}



/*
 * verify(authorityRepository, times(1)) .save(Mockito.argThat(new ArgumentMatcher<Authority>() {
 * 
 * @Override public boolean matches(Authority argument) { AuthorityKey key =
 * argument.getKeys().get(0); if (!key.getKeyId().equals("1")) return false; if
 * (!key.getJwk().equals("jwk")) return false; if (key.getValidFrom() != utc) return false; return
 * true; } }));
 */
