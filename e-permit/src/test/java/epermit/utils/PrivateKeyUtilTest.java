package epermit.utils;

import static org.mockito.Mockito.when;
import com.nimbusds.jose.jwk.ECKey;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import epermit.models.dtos.PrivateKey;
import epermit.models.EPermitProperties;
import epermit.repositories.PrivateKeyRepository;
import lombok.SneakyThrows;

@ExtendWith(MockitoExtension.class)
public class PrivateKeyUtilTest {
    @Mock
    EPermitProperties properties;

    @Mock
    PrivateKeyRepository keyRepository;

    @InjectMocks
    PrivateKeyUtil util;

    @Test
    @SneakyThrows
    void keyShouldBeCreatedWhenSaltAndPasswordIsCorrect() {
        when(properties.getKeyPassword()).thenReturn("123456");
        PrivateKey key = util.create("1");
        Assertions.assertNotNull(key.getSalt());
    }

    @Test
    @SneakyThrows
    void keyShouldNotBeCreatedWhenPasswordIsIncorrect() {
        when(properties.getKeyPassword()).thenReturn("123456");
        PrivateKey key = util.create("1");
        epermit.entities.PrivateKey keyEntity = new epermit.entities.PrivateKey();
        keyEntity.setPrivateJwk(key.getPrivateJwk());
        keyEntity.setKeyId(key.getKeyId());
        keyEntity.setSalt(key.getSalt());
        Assertions.assertThrows(IllegalStateException.class, () -> {
            when(keyRepository.findFirstByEnabledTrueOrderByIdDesc()).thenReturn(keyEntity);
            when(properties.getKeyPassword()).thenReturn("1234567");
            ECKey ecKey = util.getKey();
            Assertions.assertNotNull(ecKey);
            Assertions.assertEquals("1", ecKey.getKeyID());
        });
    }
   
}

