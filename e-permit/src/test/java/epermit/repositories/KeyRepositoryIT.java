package epermit.repositories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import java.util.Map;
import java.util.Optional;
import com.nimbusds.jose.jwk.ECKey;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import epermit.entities.Key;
import epermit.models.EPermitProperties;
import epermit.utils.KeyUtil;
import lombok.SneakyThrows;

@DataJpaTest
public class KeyRepositoryIT {
    @Autowired
    private KeyRepository keyRepository;

    @Mock
    private EPermitProperties properties;

    @Test
    @SneakyThrows
    void test() {
        when(properties.getKeyPassword()).thenReturn("123456");
        Map<String, Object> keyProps = Map.of("kty", "EC", "crv", "P-256", "x", "b-twdhMdnpLQJ_pQx8meWsvevCyD0sufkdgF9nIsX-U", "y",
                "U339OypYc4efK_xKJqnGSgWbLQ--47sCfpu-pJU2620", "d",
                "bC5gHVXyYYG0WNagcK5HUJ04be_gqtMyqYHIztk5h68", "use", "sig", "kid", "1", "alg",
                "ES256");
        ECKey ecKey = ECKey.parse(keyProps);
        KeyUtil keyUtil = new KeyUtil(properties, keyRepository);
        Key key = keyUtil.create(ecKey);
        key.setEnabled(true);
        keyRepository.save(key);
        Optional<Key> keyR = keyRepository.findOneByKeyId("1");
        assertTrue(keyR.isPresent());
    }

    @Test
    void findFirstByEnabledTrueOrderByIdTest(){
        when(properties.getKeyPassword()).thenReturn("123456");
        KeyUtil keyUtil = new KeyUtil(properties, keyRepository);
        Key key = keyUtil.create("1");
        key.setEnabled(true);
        keyRepository.save(key);
        Key key2 = keyUtil.create("2");
        key2.setEnabled(true);
        keyRepository.save(key2);
        Key k = keyRepository.findFirstByEnabledTrueOrderByIdDesc();
        assertEquals("2", k.getKeyId());
    }

}

