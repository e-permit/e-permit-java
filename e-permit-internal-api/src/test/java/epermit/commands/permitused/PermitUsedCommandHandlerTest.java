package epermit.commands.permitused;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import epermit.common.CommandResult;
import epermit.entities.Permit;
import epermit.events.AppEventPublisher;
import epermit.events.permitused.PermitUsedEventFactory;
import epermit.repositories.PermitRepository;
import epermit.services.EventService;

@ExtendWith(MockitoExtension.class)
public class PermitUsedCommandHandlerTest {
    @Mock
    PermitRepository repository;
    @Mock
    PermitUsedEventFactory factory;
    @Mock
    AppEventPublisher eventPublisher;
    @Mock
    EventService eventService;

    @InjectMocks
    PermitUsedCommandHandler handler;

    @Test
    void handleTest() {
        Permit permit = new Permit();
        when(repository.findOneByPermitId("ABC")).thenReturn(Optional.of(permit));
        PermitUsedCommand command = new PermitUsedCommand();
        command.setId("ABC");
        CommandResult r = handler.handle(command);
        assertTrue(r.isOk());
    }
}
