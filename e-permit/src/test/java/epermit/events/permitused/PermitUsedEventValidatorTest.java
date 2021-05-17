package epermit.events.permitused;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import epermit.events.EventValidationResult;
import epermit.repositories.IssuedPermitRepository;
import epermit.utils.GsonUtil;

@ExtendWith(MockitoExtension.class)
public class PermitUsedEventValidatorTest {
    @Mock
    IssuedPermitRepository issuedPermitRepository;

    @InjectMocks
    PermitUsedEventValidator validator;

    @Test
    void okTest() {
        PermitUsedEvent event = new PermitUsedEvent();
        event.setIssuer("TR");
        event.setIssuedFor("UA");
        event.setPermitId("TR-UA");
        event.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC).toEpochSecond());
        String payload = GsonUtil.getGson().toJson(event);
        when(issuedPermitRepository.existsByIssuedForAndPermitId(anyString(), anyString()))
                .thenReturn(true);
        EventValidationResult r = validator.validate(payload);
        assertTrue(r.isOk());
    }

    @Test
    void invalidPermitIdOrIssuerTest() {
        PermitUsedEvent event = new PermitUsedEvent();
        event.setIssuer("TR");
        event.setIssuedFor("UA");
        event.setPermitId("TR-UA");
        event.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC).toEpochSecond());
        String payload = GsonUtil.getGson().toJson(event);
        when(issuedPermitRepository.existsByIssuedForAndPermitId(anyString(), anyString()))
                .thenReturn(false);
        EventValidationResult r = validator.validate(payload);
        assertFalse(r.isOk());
        assertEquals("INVALID_PERMITID_OR_ISSUER", r.getErrorCode());
    }

}
