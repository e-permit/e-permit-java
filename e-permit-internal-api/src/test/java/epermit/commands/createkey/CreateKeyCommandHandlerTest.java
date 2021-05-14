package epermit.commands.createkey;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import epermit.common.CommandResult;
import epermit.repositories.KeyRepository;
import epermit.services.KeyService;

@ExtendWith(MockitoExtension.class)
public class CreateKeyCommandHandlerTest {
    @Mock
    KeyRepository repository;

    @Mock
    KeyService keyService;

    @InjectMocks
    CreateKeyCommandHandler handler;

    @Test
    void handleTest() {
        CreateKeyCommand command = new CreateKeyCommand();
        command.setKeyId("keyId");
        CommandResult r = handler.handle(command);
        assertTrue(r.isOk());
    }
}
