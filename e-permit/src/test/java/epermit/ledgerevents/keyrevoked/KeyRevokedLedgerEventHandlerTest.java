package epermit.ledgerevents.keyrevoked;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import epermit.commons.EpermitValidationException;
import epermit.commons.ErrorCodes;
import epermit.commons.GsonUtil;
import epermit.entities.LedgerPublicKey;
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
        handler.handle(GsonUtil.toMap(event));
    }

    @Test
    void handleInsufficientKeyTest() {
        KeyRevokedLedgerEvent event = new KeyRevokedLedgerEvent("TR", "UZ", "0");
        event.setKeyId("1");
        LedgerPublicKey pubKey1 = new LedgerPublicKey();
        pubKey1.setAuthorityCode("TR");
        pubKey1.setKeyId("1");
        when(publicKeyRepository.findAllByAuthorityCodeAndRevokedFalse("TR"))
                .thenReturn(List.of(pubKey1));
        EpermitValidationException ex =
                Assertions.assertThrows(EpermitValidationException.class, () -> {
                    handler.handle(GsonUtil.toMap(event));
                });
        assertEquals(ErrorCodes.INSUFFICIENT_KEY.name(), ex.getErrorCode());
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
        EpermitValidationException ex =
                Assertions.assertThrows(EpermitValidationException.class, () -> {
                    handler.handle(GsonUtil.toMap(event));
                });
        assertEquals(ErrorCodes.KEY_NOTFOUND.name(), ex.getErrorCode());
    }
}
