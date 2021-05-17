package epermit.events.permitrevoked;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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
        String payload = GsonUtil.getGson().toJson(event);
        when(permitRepository.existsByIssuerAndPermitId(anyString(), anyString())).thenReturn(true);
        EventValidationResult r = validator.validate(payload);
        assertTrue(r.isOk());
    }

    @Test
    void invalidPermitIdOrIssuerTest() {
        PermitRevokedEvent event = new PermitRevokedEvent();
        event.setPermitId("UA-TR-2021-1-1");
        event.setIssuer("UA");
        event.setIssuedFor("TR");
        String payload = GsonUtil.getGson().toJson(event);
        EventValidationResult r = validator.validate(payload);
        when(permitRepository.existsByIssuerAndPermitId(anyString(), anyString())).thenReturn(true);
        assertFalse(r.isOk());
        assertEquals("INVALID_PERMITID_OR_ISSUER", r.getErrorCode());
    }
}
