package epermit.commands.enablequota;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import epermit.common.CommandResult;
import epermit.entities.VerifierQuota;
import epermit.events.AppEventPublisher;
import epermit.events.quotacreated.QuotaCreatedEventFactory;
import epermit.repositories.VerifierQuotaRepository;
import epermit.services.EventService;

@ExtendWith(MockitoExtension.class)
public class EnableQuotaCommandHandlerTest {
    @Mock
    VerifierQuotaRepository repository;
    @Mock
    QuotaCreatedEventFactory factory;
    @Mock
    AppEventPublisher eventPublisher;
    @Mock
    EventService eventService;

    @InjectMocks
    EnableQuotaCommandHandler handler;

    @Test
    void handleTest() {
        VerifierQuota quota = new VerifierQuota();
        when(repository.findById(1)).thenReturn(Optional.of(quota));
        EnableQuotaCommand command = new EnableQuotaCommand();
        command.setId(1);
        CommandResult r = handler.handle(command);
        assertTrue(r.isOk());
    }
}
