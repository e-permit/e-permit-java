package epermit.services;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import epermit.entities.Authority;
import epermit.entities.Key;
import epermit.events.keycreated.KeyCreatedEventFactory;
import epermit.repositories.AuthorityRepository;
import epermit.repositories.KeyRepository;
import epermit.utils.KeyUtil;

@ExtendWith(MockitoExtension.class)
public class KeyServiceTest {
    @Mock
    private KeyRepository keyRepository;

    @Mock
    private AuthorityRepository authorityRepository;

    @Mock
    private KeyUtil keyUtil;

    @Mock
    private KeyCreatedEventFactory factory;

    @InjectMocks
    private KeyService keyService;

    @Test
    void seedTest() {
        when(keyRepository.count()).thenReturn(Long.valueOf(0));
        Key key = new Key();
        when(keyUtil.create("1")).thenReturn(key);
        keyService.seed();
        verify(keyRepository).save(key);
    }

    @Test
    void seedKeyExistTest() {
        when(keyRepository.count()).thenReturn(Long.valueOf(1));
        keyService.seed();
        verify(keyRepository, never()).save(any());
    }

    @Test
    void createTest() {
        when(keyRepository.findOneByKeyId("1")).thenReturn(Optional.empty());
        Key key = new Key();
        key.setKeyId("1");
        when(keyUtil.create("1")).thenReturn(key);
        keyService.create("1");
        verify(keyRepository).save(key);
    }
    
    @Test
    void createKeyExistTest() {
        Key key = new Key();
        key.setKeyId("1");
        when(keyRepository.findOneByKeyId("1")).thenReturn(Optional.of(key));
        assertThrows(ResponseStatusException.class, () -> {
            keyService.create("1");
        });
        
    }

    @Test
    void enableTest() {
        Key key = new Key();
        Key existKey = new Key();
        existKey.setEnabled(true);
        Authority authority = new Authority();
        authority.setCode("TR");
        when(keyRepository.findById(1)).thenReturn(Optional.of(key));
        when(authorityRepository.findAll()).thenReturn(List.of(authority));
        keyService.enable(1);
        assertTrue(key.isEnabled());
        verify(keyRepository, times(1)).save(key);
        verify(factory).create(key, "TR");
    }
    
    @Test
    void enableKeyNotFoundTest() {
        when(keyRepository.findById(1)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> {
            keyService.enable(1);
        });
    }
}
