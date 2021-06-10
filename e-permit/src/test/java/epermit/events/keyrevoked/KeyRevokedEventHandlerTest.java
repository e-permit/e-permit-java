package epermit.events.keyrevoked;

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
import epermit.entities.AuthorityKey;
import epermit.repositories.AuthorityKeyRepository;

@ExtendWith(MockitoExtension.class)
public class KeyRevokedEventHandlerTest {
    @Mock
    AuthorityKeyRepository authorityKeyRepository;

    @InjectMocks
    KeyRevokedEventHandler handler;

    @Test
    void handleTest() {
        KeyRevokedEvent event = new KeyRevokedEvent();
        event.setKeyId("1");
        event.setIssuer("UA");
        event.setIssuedFor("TR");
        when(authorityKeyRepository.findOneByIssuerAndKeyId("UA", "1"))
                .thenReturn(Optional.of(new AuthorityKey()));
        handler.handle(event);
        verify(authorityKeyRepository, times(1)).save(any());
    }

}
