package epermit.ledger.utils;

import static org.mockito.Mockito.when;
import java.util.Optional;
import com.nimbusds.jose.jwk.ECKey;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import epermit.ledger.entities.PrivateKey;
import epermit.ledger.models.EPermitProperties;
import epermit.ledger.repositories.PrivateKeyRepository;
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
        Assertions.assertThrows(IllegalStateException.class, () -> {
            when(keyRepository.findFirstByEnabledTrueOrderByIdDesc()).thenReturn(key);
            when(properties.getKeyPassword()).thenReturn("1234567");
            ECKey ecKey = util.getKey();
            Assertions.assertNotNull(ecKey);
            Assertions.assertEquals("1", ecKey.getKeyID());
        });
    }
   
}

