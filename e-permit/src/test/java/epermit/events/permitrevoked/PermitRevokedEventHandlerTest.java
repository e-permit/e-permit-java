package epermit.events.permitrevoked;

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
import epermit.entities.Permit;
import epermit.repositories.PermitRepository;

@ExtendWith(MockitoExtension.class)
public class PermitRevokedEventHandlerTest {
    @Mock
    PermitRepository permitRepository;

    @InjectMocks
    PermitRevokedEventHandler handler;

    @Test
    void handleTest() {
        PermitRevokedEvent event = new PermitRevokedEvent();
        event.setPermitId("UA-TR-2021-1-1");
        event.setIssuer("UA");
        event.setIssuedFor("TR");
        Permit p = new Permit();
        when(permitRepository.findOneByIssuerAndPermitId("UA", "UA-TR-2021-1-1"))
                .thenReturn(Optional.of(p));
        handler.handle(event);
        verify(permitRepository, times(1)).delete(any());
    }

}
