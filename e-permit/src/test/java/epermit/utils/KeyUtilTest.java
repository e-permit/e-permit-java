package epermit.utils;

import static org.mockito.Mockito.when;
import com.nimbusds.jose.jwk.ECKey;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import epermit.models.EPermitProperties;
import epermit.models.PrivateKey;
import epermit.services.KeyService;
import lombok.SneakyThrows;

@ExtendWith(MockitoExtension.class)
public class KeyUtilTest {
    @Mock
    EPermitProperties properties;

    @Mock
    KeyService service;

    @InjectMocks
    KeyUtil util;

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
            when(service.getActiveKey()).thenReturn(key);
            when(properties.getKeyPassword()).thenReturn("1234567");
            ECKey ecKey = util.getKey();
            Assertions.assertNotNull(ecKey);
            Assertions.assertEquals("1", ecKey.getKeyID());
        });
    }
   
}

