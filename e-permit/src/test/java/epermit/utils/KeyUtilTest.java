package epermit.utils;

import static org.mockito.Mockito.when;
import java.util.Optional;
import com.nimbusds.jose.jwk.ECKey;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import epermit.entities.Key;
import epermit.models.EPermitProperties;
import epermit.repositories.KeyRepository;
import lombok.SneakyThrows;

@ExtendWith(MockitoExtension.class)
public class KeyUtilTest {
    @Mock
    EPermitProperties properties;

    @Mock
    KeyRepository keyRepository;

    @InjectMocks
    KeyUtil util;

    @Test
    @SneakyThrows
    void keyShouldBeCreatedWhenSaltAndPasswordIsCorrect() {
        when(properties.getKeyPassword()).thenReturn("123456");
        Key key = util.create("1");
        Assertions.assertNotNull(key.getSalt());
    }

    @Test
    @SneakyThrows
    void keyShouldNotBeCreatedWhenPasswordIsIncorrect() {
        when(properties.getKeyPassword()).thenReturn("123456");
        Key key = util.create("1");
        Assertions.assertThrows(IllegalStateException.class, () -> {
            when(keyRepository.findFirstByEnabledTrueOrderByIdDesc()).thenReturn(key);
            when(properties.getKeyPassword()).thenReturn("1234567");
            ECKey ecKey = util.getKey();
            Assertions.assertNotNull(ecKey);
            Assertions.assertEquals("1", ecKey.getKeyID());
        });
    }
   
}

