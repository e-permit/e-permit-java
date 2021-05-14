package epermit.commands.createquota;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import epermit.common.CommandResult;
import epermit.common.PermitType;
import epermit.entities.Authority;
import epermit.repositories.AuthorityRepository;

@ExtendWith(MockitoExtension.class)
public class CreateQuotaCommandHandlerTest {
    @Mock
    AuthorityRepository repository;

    @InjectMocks
    CreateQuotaCommandHandler handler;

    @Test
    void handleTest() {
        CreateQuotaCommand command = new CreateQuotaCommand();
        command.setAuthorityCode("TR");
        command.setEndId(5);
        command.setPermitType(PermitType.BILITERAL);
        command.setPermitYear(2021);
        command.setStartId(1);
        Authority authority = new Authority();
        when(repository.findOneByCode("TR")).thenReturn(Optional.of(authority));
        CommandResult r = handler.handle(command);
        assertTrue(r.isOk());
    }
}
