package epermit.commands.enablekey;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import epermit.common.CommandResult;
import epermit.entities.Key;
import epermit.events.AppEventPublisher;
import epermit.events.keycreated.KeyCreatedEventFactory;
import epermit.repositories.AuthorityRepository;
import epermit.repositories.KeyRepository;
import epermit.services.EventService;

@ExtendWith(MockitoExtension.class)
public class EnableKeyCommandHandlerTest {
    @Mock
    KeyRepository repository;
    @Mock
    KeyCreatedEventFactory factory;
    @Mock
    AuthorityRepository authorityRepository;
    @Mock
    AppEventPublisher eventPublisher;
    @Mock
    EventService eventService;

    @InjectMocks
    EnableKeyCommandHandler handler;

    @Test
    void handleTest() {
        Key key = new Key();
        when(repository.findOneByKeyId("keyId")).thenReturn(Optional.of(key));
        EnableKeyCommand command = new EnableKeyCommand();
        command.setId("keyId");
        CommandResult r = handler.handle(command);
        assertTrue(r.isOk());
    }
}
