package epermit.ledgerevents.keyrevoked;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import epermit.entities.LedgerPublicKey;
import epermit.ledgerevents.LedgerEventHandleResult;
import epermit.repositories.LedgerPublicKeyRepository;

@ExtendWith(MockitoExtension.class)
public class KeyRevokedLedgerEventHandlerTest {

    @Mock
    LedgerPublicKeyRepository publicKeyRepository;

    @InjectMocks
    KeyRevokedLedgerEventHandler handler;

    @Test
    void handleOkTest() {
        KeyRevokedLedgerEvent event = new KeyRevokedLedgerEvent("TR", "UZ", "0");
        event.setKeyId("1");
        LedgerPublicKey pubKey1 = new LedgerPublicKey();
        pubKey1.setAuthorityCode("TR");
        pubKey1.setKeyId("1");
        LedgerPublicKey pubKey2 = new LedgerPublicKey();
        pubKey2.setAuthorityCode("TR");
        pubKey2.setKeyId("2");
        when(publicKeyRepository.findAllByAuthorityCodeAndRevokedFalse("TR"))
                .thenReturn(List.of(pubKey1, pubKey2));
        LedgerEventHandleResult r = handler.handle(event);
        assertTrue(r.isOk());
    }

    @Test
    void handleThereIsOnlyOneKeyTest() {
        KeyRevokedLedgerEvent event = new KeyRevokedLedgerEvent("TR", "UZ", "0");
        event.setKeyId("1");
        LedgerPublicKey pubKey1 = new LedgerPublicKey();
        pubKey1.setAuthorityCode("TR");
        pubKey1.setKeyId("1");
        when(publicKeyRepository.findAllByAuthorityCodeAndRevokedFalse("TR"))
                .thenReturn(List.of(pubKey1));
        LedgerEventHandleResult r = handler.handle(event);
        assertFalse(r.isOk());
        assertEquals("THERE_IS_ONLY_ONE_KEY", r.getErrorCode());
    }

    @Test
    void handleKeyNotFoundTest() {
        KeyRevokedLedgerEvent event = new KeyRevokedLedgerEvent("TR", "UZ", "0");
        event.setKeyId("1");
        LedgerPublicKey pubKey1 = new LedgerPublicKey();
        pubKey1.setAuthorityCode("TR");
        pubKey1.setKeyId("2");
        LedgerPublicKey pubKey2 = new LedgerPublicKey();
        pubKey2.setAuthorityCode("TR");
        pubKey2.setKeyId("3");
        when(publicKeyRepository.findAllByAuthorityCodeAndRevokedFalse("TR"))
                .thenReturn(List.of(pubKey1, pubKey2));
        LedgerEventHandleResult r = handler.handle(event);
        assertFalse(r.isOk());
        assertEquals("KEY_NOTFOUND", r.getErrorCode());
    }

    /*
     * @Test void okTest() { KeyRevokedEvent event = new KeyRevokedEvent(); event.setKeyId("1");
     * event.setIssuer("UA"); event.setIssuedFor("TR"); Authority authority = new Authority();
     * AuthorityKey key = new AuthorityKey(); key.setKeyId("1"); authority.addKey(key);
     * authority.addKey(new AuthorityKey());
     * when(authorityRepository.findOneByCode("UA")).thenReturn(authority); EventValidationResult r
     * = validator.validate(GsonUtil.toMap(event)); assertTrue(r.isOk()); }
     * 
     * @Test void invalidPermitIdOrIssuerTest() { /*PermitRevokedEvent event = new
     * PermitRevokedEvent(); event.setPermitId("UA-TR-2021-1-1"); event.setIssuer("UA");
     * event.setIssuedFor("TR"); EventValidationResult r =
     * validator.validate(GsonUtil.toMap(event)); assertFalse(r.isOk());
     * assertEquals("INVALID_PERMITID_OR_ISSUER", r.getErrorCode()); }
     */

}
