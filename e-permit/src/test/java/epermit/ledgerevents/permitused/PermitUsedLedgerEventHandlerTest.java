package epermit.ledgerevents.permitused;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
import epermit.entities.LedgerPermit;

@ExtendWith(MockitoExtension.class)
public class PermitUsedLedgerEventHandlerTest {

    @InjectMocks
    PermitUsedLedgerEventHandler handler;

    @Captor
    ArgumentCaptor<LedgerPermit> captor;

    /*@Test
    void handleTest() {
        PermitUsedEvent event = new PermitUsedEvent();
        event.setIssuer("TR");
        event.setIssuedFor("UA");
        event.setPermitId("TR-UA");
        event.setActivityType(PermitActivityType.ENTERANCE);
        event.setCreatedAt(Instant.now().getEpochSecond());
        when(issuedPermitRepository.findOneByIssuedForAndPermitId("UA", "TR-UA"))
                .thenReturn(Optional.of(new IssuedPermit()));
        handler.handle(event);
        verify(issuedPermitRepository).save(captor.capture());
        IssuedPermit permit = captor.getValue();
        IssuedPermitActivity activity = permit.getActivities().get(0);
        assertTrue(permit.isUsed());
        assertEquals(event.getActivityType(), activity.getActivityType());
        //assertNotNull(activity.getCreatedAt());
    }

    @Test
    void okTest() {
        PermitUsedEvent event = new PermitUsedEvent();
        event.setIssuer("TR");
        event.setIssuedFor("UA");
        event.setPermitId("TR-UA");
        event.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC).toEpochSecond());
        when(issuedPermitRepository.existsByIssuedForAndPermitId(anyString(), anyString()))
                .thenReturn(true);
        EventValidationResult r = validator.validate(GsonUtil.toMap(event));
        assertTrue(r.isOk());
    }

    @Test
    void invalidPermitIdOrIssuerTest() {
        PermitUsedEvent event = new PermitUsedEvent();
        event.setIssuer("TR");
        event.setIssuedFor("UA");
        event.setPermitId("TR-UA");
        event.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC).toEpochSecond());
        when(issuedPermitRepository.existsByIssuedForAndPermitId(anyString(), anyString()))
                .thenReturn(false);
        EventValidationResult r = validator.validate(GsonUtil.toMap(event));
        assertFalse(r.isOk());
        assertEquals("INVALID_PERMITID_OR_ISSUER", r.getErrorCode());
    }*/
}
