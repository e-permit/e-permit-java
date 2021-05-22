package epermit.events.permitrevoked;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import epermit.events.EventValidationResult;
import epermit.repositories.PermitRepository;
import epermit.utils.GsonUtil;

@ExtendWith(MockitoExtension.class)
public class PermitRevokedEventValidatorTest {
    @Mock
    PermitRepository permitRepository;

    @InjectMocks
    PermitRevokedEventValidator validator;

    @Test
    void okTest() {
        PermitRevokedEvent event = new PermitRevokedEvent();
        event.setPermitId("UA-TR-2021-1-1");
        event.setIssuer("UA");
        event.setIssuedFor("TR");
        when(permitRepository.existsByIssuerAndPermitId("UA", "UA-TR-2021-1-1")).thenReturn(true);
        EventValidationResult r = validator.validate(GsonUtil.toMap(event));
        assertTrue(r.isOk());
    }

    @Test
    void invalidPermitIdOrIssuerTest() {
        PermitRevokedEvent event = new PermitRevokedEvent();
        event.setPermitId("UA-TR-2021-1-1");
        event.setIssuer("UA");
        event.setIssuedFor("TR");
        EventValidationResult r = validator.validate(GsonUtil.toMap(event));
        assertFalse(r.isOk());
        assertEquals("INVALID_PERMITID_OR_ISSUER", r.getErrorCode());
    }
}
