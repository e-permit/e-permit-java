package epermit.ledger.ledgerevents.keyrevoked;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import epermit.entities.Authority;
import epermit.entities.AuthorityKey;
import epermit.events.EventValidationResult;
import epermit.repositories.AuthorityRepository;
import epermit.utils.GsonUtil;

@ExtendWith(MockitoExtension.class)
public class KeyRevokedEventValidatorTest {
    @Mock
    AuthorityRepository authorityRepository;

    @InjectMocks
    KeyRevokedEventValidator validator;

    @Test
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
        assertEquals("INVALID_PERMITID_OR_ISSUER", r.getErrorCode());*/
    }
}
