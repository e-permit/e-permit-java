package epermit.repositories;


import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import com.nimbusds.jose.jwk.ECKey;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import epermit.entities.Key;
import epermit.models.EPermitProperties;
import epermit.utils.JwsUtil;
import epermit.utils.KeyUtil;
import epermit.utils.PermitUtil;
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
        JwsUtil jwsUtil = new JwsUtil(keyUtil, properties, null, null);
        PermitUtil permitUtil = new PermitUtil(jwsUtil, null, null);
        Key key = keyUtil.create(ecKey);
        key.setActive(true);
        keyRepository.save(key);
        Optional<Key> keyR = keyRepository.findOneByKeyId("1");
        assertTrue(keyR.isPresent());
    }

}

