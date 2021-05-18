package epermit.events.permitcreated;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import epermit.entities.Permit;
import epermit.models.enums.PermitType;
import epermit.repositories.PermitRepository;

@ExtendWith(MockitoExtension.class)
public class PermitCreatedEventHandlerTest {
    @Mock
    PermitRepository permitRepository;

    @InjectMocks
    PermitCreatedEventHandler handler;

    @Captor
    ArgumentCaptor<Permit> captor;

    @Test
    void handleTest() {
        PermitCreatedEvent event = new PermitCreatedEvent();
        event.setExpireAt("A");
        event.setIssuedAt("A");
        event.setCompanyName("A");
        event.setPermitId("UA-TR-2021-1-1");
        event.setPermitType(PermitType.BILITERAL);
        event.setPermitYear(2021);
        event.setPlateNumber("A");
        event.setSerialNumber(1);
        event.setIssuer("UA");
        event.setIssuedFor("TR");
        handler.handle(event);
        verify(permitRepository).save(captor.capture());
        Permit p = captor.getValue();
        assertEquals("A", p.getExpireAt());
        assertEquals("UA-TR-2021-1-1", p.getPermitId());
        assertEquals(PermitType.BILITERAL, p.getPermitType());
        assertEquals("A", p.getCompanyName());
        assertEquals("A", p.getIssuedAt());
        assertEquals("UA", p.getIssuer());
        assertEquals(2021, p.getPermitYear());
        assertEquals(1, p.getSerialNumber());
    }
}
