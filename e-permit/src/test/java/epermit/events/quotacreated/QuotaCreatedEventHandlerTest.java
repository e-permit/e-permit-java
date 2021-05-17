package epermit.events.quotacreated;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import epermit.entities.Authority;
import epermit.entities.IssuerQuota;
import epermit.events.EventValidationResult;
import epermit.models.PermitType;
import epermit.repositories.AuthorityRepository;

@ExtendWith(MockitoExtension.class)
public class QuotaCreatedEventHandlerTest {
    @Mock
    AuthorityRepository authorityRepository;

    @InjectMocks
    QuotaCreatedEventHandler handler;

    @Captor
    ArgumentCaptor<Authority> captor;

    @Test
    void handleTest() {
        QuotaCreatedEvent event = new QuotaCreatedEvent();
        event.setIssuer("TR");
        event.setIssuedFor("UA");
        event.setStartNumber(4);
        event.setEndNumber(40);
        event.setPermitType(PermitType.BILITERAL);
        event.setPermitYear(2021);
        when(authorityRepository.findOneByCode("TR")).thenReturn(Optional.of(new Authority()));
        handler.handle(event);
        verify(authorityRepository, times(1)).save(captor.capture());
        IssuerQuota quota = captor.getValue().getIssuerQuotas().get(0);
        assertEquals(4, quota.getStartNumber());
        assertEquals(40, quota.getEndNumber());
        assertEquals(2021, quota.getPermitYear());
        assertEquals(PermitType.BILITERAL, quota.getPermitType());
    }
}
