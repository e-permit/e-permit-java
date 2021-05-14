package epermit.commands.revokepermit;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import epermit.common.CommandResult;
import epermit.entities.IssuedPermit;
import epermit.events.AppEventPublisher;
import epermit.events.permitrevoked.PermitRevokedEventFactory;
import epermit.repositories.IssuedPermitRepository;
import epermit.repositories.PermitRepository;
import epermit.services.EventService;

@ExtendWith(MockitoExtension.class)
public class RevokePermitCommandHandlerTest {
    @Mock
    IssuedPermitRepository repository;
    @Mock
    PermitRevokedEventFactory factory;
    @Mock
    AppEventPublisher eventPublisher;
    @Mock
    EventService eventService;

    @InjectMocks
    RevokePermitCommandHandler handler;

    @Test
    void handleTest() {
        IssuedPermit permit = new IssuedPermit();
        when(repository.findOneByPermitId("ABC")).thenReturn(Optional.of(permit));
        RevokePermitCommand command = new  RevokePermitCommand();
        command.setId("ABC");
        command.setComment("comment");
        CommandResult r = handler.handle(command);
        assertTrue(r.isOk());
    }
}
