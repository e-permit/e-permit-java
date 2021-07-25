package epermit.services;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.util.Pair;
import org.springframework.web.server.ResponseStatusException;
import epermit.entities.PrivateKey;
import epermit.models.EPermitProperties;
import epermit.repositories.AuthorityRepository;
import epermit.repositories.PrivateKeyRepository;
import epermit.utils.PrivateKeyUtil;

@ExtendWith(MockitoExtension.class)
public class PrivateKeyServiceTest {
    @Mock
    PrivateKeyRepository keyRepository;

    @Mock
    AuthorityRepository authorityRepository;

    @Mock
    PrivateKeyUtil keyUtil;

    @Mock
    EPermitProperties properties;

    @InjectMocks
    PrivateKeyService keyService;

    @Test
    void seedTest() {
        when(keyRepository.count()).thenReturn(Long.valueOf(0));
        PrivateKey key = new PrivateKey();
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
        PrivateKey key = new PrivateKey();
        key.setKeyId("1");
        when(keyUtil.create("1")).thenReturn(key);
        keyService.create("1");
        verify(keyRepository).save(key);
    }
    
    @Test
    void createKeyExistTest() {
        PrivateKey key = new PrivateKey();
        key.setKeyId("1");
        when(keyRepository.findOneByKeyId("1")).thenReturn(Optional.of(key));
        assertThrows(ResponseStatusException.class, () -> {
            keyService.create("1");
        });
        
    }
}
