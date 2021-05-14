package epermit.commands.createpermit;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import epermit.common.CommandResult;
import epermit.common.PermitProperties;
import epermit.common.PermitType;
import epermit.events.AppEventPublisher;
import epermit.events.permitcreated.PermitCreatedEventFactory;
import epermit.repositories.IssuedPermitRepository;
import epermit.services.EventService;
import epermit.services.PermitService;

@ExtendWith(MockitoExtension.class)
public class CreatePermitCommandHandlerTest {

    @Mock IssuedPermitRepository repository;
    @Mock PermitProperties properties;
    @Mock PermitCreatedEventFactory factory;
    @Mock AppEventPublisher eventPublisher;
    @Mock PermitService permitService;
    @Mock EventService eventService;

    @InjectMocks
    CreatePermitCommandHandler handler;

    @Test
    void handleTest() {
        CreatePermitCommand command = new CreatePermitCommand();
        command.setCompanyName("TR");
        command.setIssuedFor("UA");
        command.setPermitType(PermitType.BILITERAL);
        command.setPermitYear(2021);
        command.setPlateNumber("08hh");
        //when(repository.findOneByCode("TR")).thenReturn(Optional.of(authority));
        CommandResult r = handler.handle(command);
        assertTrue(r.isOk());
    }
}
