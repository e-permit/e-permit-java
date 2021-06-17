package epermit.ledger.ledgerevents.keyrevoked;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class KeyRevokedLedgerEventHandlerTest {

    @InjectMocks
    KeyRevokedLedgerEventHandler handler;

    @Test
    void handleOkTest() {
        KeyRevokedLedgerEvent event = new KeyRevokedLedgerEvent();
        event.setKeyId("1");
        event.setIssuer("UA");
        event.setIssuedFor("TR");
        /*when(authorityKeyRepository.findOneByIssuerAndKeyId("UA", "1"))
                .thenReturn(Optional.of(new AuthorityKey()));
        handler.handle(event);
        verify(authorityKeyRepository, times(1)).save(any());*/
    }

    /*@Test
    void okTest() {
        KeyRevokedEvent event = new KeyRevokedEvent();
        event.setKeyId("1");
        event.setIssuer("UA");
        event.setIssuedFor("TR");
        Authority authority = new Authority();
        AuthorityKey key = new AuthorityKey();
        key.setKeyId("1");
        authority.addKey(key);
        authority.addKey(new AuthorityKey());
        when(authorityRepository.findOneByCode("UA")).thenReturn(authority);
        EventValidationResult r = validator.validate(GsonUtil.toMap(event));
        assertTrue(r.isOk());
    }

    @Test
    void invalidPermitIdOrIssuerTest() {
        /*PermitRevokedEvent event = new PermitRevokedEvent();
        event.setPermitId("UA-TR-2021-1-1");
        event.setIssuer("UA");
        event.setIssuedFor("TR");
        EventValidationResult r = validator.validate(GsonUtil.toMap(event));
        assertFalse(r.isOk());
        assertEquals("INVALID_PERMITID_OR_ISSUER", r.getErrorCode());
    }*/

}
