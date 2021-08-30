package epermit.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import epermit.models.EPermitProperties;
import epermit.repositories.AuthorityRepository;
import epermit.repositories.LedgerPublicKeyRepository;
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

    @Mock
    LedgerPublicKeyRepository ledgerPublicKeyRepository;

    @InjectMocks
    PrivateKeyService keyService;

    @Test
    void seedTest() {
        when(keyRepository.count()).thenReturn(Long.valueOf(0));
        epermit.models.dtos.PrivateKey key = new epermit.models.dtos.PrivateKey();
        key.setKeyId("1");
        when(keyUtil.create("1")).thenReturn(key);
        keyService.seed();
        verify(keyRepository).save(any());
    }

    @Test
    void seedKeyExistTest() {
        when(keyRepository.count()).thenReturn(Long.valueOf(1));
        keyService.seed();
        verify(keyRepository, never()).save(any());
    }

    @Test
    void createTest() {
        epermit.models.dtos.PrivateKey key = new epermit.models.dtos.PrivateKey();
        key.setKeyId("1");
        when(keyUtil.create("1")).thenReturn(key);
        keyService.create("1");
        verify(keyRepository).save(any());
    }
    
}
