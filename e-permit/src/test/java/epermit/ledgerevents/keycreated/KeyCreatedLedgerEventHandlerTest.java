package epermit.ledgerevents.keycreated;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import javax.validation.Validation;
import javax.validation.Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import epermit.commons.EpermitValidationException;
import epermit.commons.ErrorCodes;
import epermit.commons.GsonUtil;
import epermit.entities.LedgerPublicKey;
import epermit.ledgerevents.LedgerEventHandleResult;
import epermit.repositories.LedgerPublicKeyRepository;

@ExtendWith(MockitoExtension.class)

public class KeyCreatedLedgerEventHandlerTest {
    @Mock
    LedgerPublicKeyRepository keyRepository;

    @Spy
    ModelMapper modelMapper = new ModelMapper();

    @InjectMocks
    KeyCreatedLedgerEventHandler handler;

    @Captor
    ArgumentCaptor<LedgerPublicKey> captor;


    @Test
    void okTest() {
        KeyCreatedLedgerEvent event = new KeyCreatedLedgerEvent("TR", "UZ", "0");
        event.setKid("1");
        event.setAlg("ES256");
        event.setCrv("P-256");
        event.setKty("EC");
        event.setUse("sig");
        event.setX("b-twdhMdnpLQJ_pQx8meWsvevCyD0sufkdgF9nIsX-U");
        event.setY("U339OypYc4efK_xKJqnGSgWbLQ--47sCfpu-pJU2620");
        LedgerEventHandleResult r = handler.handle(GsonUtil.toMap(event));
        assertTrue(r.isOk());
        verify(keyRepository, times(1)).save(captor.capture());
        assertEquals("1", captor.getValue().getKeyId());
        assertEquals("TR", captor.getValue().getAuthorityCode());
    }

    @Test
    void keyExistTest() {
        KeyCreatedLedgerEvent event = new KeyCreatedLedgerEvent("TR", "UZ", "0");
        event.setKid("1");
        when(keyRepository.existsByAuthorityCodeAndKeyId("TR", "1")).thenReturn(true);
        EpermitValidationException ex = Assertions.assertThrows(EpermitValidationException.class, () -> {
            handler.handle(GsonUtil.toMap(event));
        });
        assertEquals(ErrorCodes.KEYID_ALREADY_EXISTS.name(), ex.getErrorCode());
        verify(keyRepository, never()).save(any());
    }



    /*
     * @Test void okTest() { String jwk =
     * "{\"kty\":\"EC\",\"crv\":\"P-256\",\"x\":\"b-twdhMdnpLQJ_pQx8meWsvevCyD0sufkdgF9nIsX-U\",\"y\":\"U339OypYc4efK_xKJqnGSgWbLQ--47sCfpu-pJU2620\",\"use\":\"sig\",\"kid\":\"1\",\"alg\":\"ES256\"}";
     * KeyCreatedEvent event = new KeyCreatedEvent(); event.setJwk(GsonUtil.getGson().fromJson(jwk,
     * PublicJwk.class)); event.setIssuer("UA"); EventValidationResult r =
     * validator.validate(GsonUtil.toMap(event)); assertTrue(r.isOk()); }
     * 
     * @Test void invalidJwkTest() { String jwk =
     * "{\"kty\":\"EC\",\"crv\":\"P-256\",\"x\":\"\",\"y\":\"U339OypYc4efK_xKJqnGSgWbLQ--47sCfpu-pJU2620\",\"use\":\"sig\",\"kid\":\"1\",\"alg\":\"ES256\"}";
     * KeyCreatedEvent event = new KeyCreatedEvent(); event.setJwk(GsonUtil.getGson().fromJson(jwk,
     * PublicJwk.class)); event.setIssuer("UA"); EventValidationResult r =
     * validator.validate(GsonUtil.toMap(event)); assertFalse(r.isOk()); assertEquals("INVALID_KEY",
     * r.getErrorCode()); }
     */
}

/*
 * verify(authorityRepository, times(1)) .save(Mockito.argThat(new ArgumentMatcher<Authority>() {
 * 
 * @Override public boolean matches(Authority argument) { AuthorityKey key =
 * argument.getKeys().get(0); if (!key.getKeyId().equals("1")) return false; if
 * (!key.getJwk().equals("jwk")) return false; if (key.getValidFrom() != utc) return false; return
 * true; } }));
 */
