package epermit.events.permitused;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import epermit.entities.IssuedPermit;
import epermit.entities.IssuedPermitActivity;
import epermit.models.enums.PermitActivityType;
import epermit.repositories.IssuedPermitRepository;

@ExtendWith(MockitoExtension.class)
public class PermitUsedEventHandlerTest {
    @Mock
    IssuedPermitRepository issuedPermitRepository;

    @InjectMocks
    PermitUsedEventHandler handler;

    @Captor
    ArgumentCaptor<IssuedPermit> captor;

    @Test
    void handleTest() {
        PermitUsedEvent event = new PermitUsedEvent();
        event.setIssuer("TR");
        event.setIssuedFor("UA");
        event.setPermitId("TR-UA");
        event.setActivityType(PermitActivityType.ENTERANCE);
        event.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC).toEpochSecond());
        when(issuedPermitRepository.findOneByIssuedForAndPermitId("UA", "TR-UA"))
                .thenReturn(Optional.of(new IssuedPermit()));
        handler.handle(event);
        verify(issuedPermitRepository).save(captor.capture());
        IssuedPermit permit = captor.getValue();
        IssuedPermitActivity activity = permit.getActivities().get(0);
        assertTrue(permit.isUsed());
        assertEquals(event.getActivityType(), activity.getActivityType());
        assertNotNull(activity.getCreatedAt());
    }
}
