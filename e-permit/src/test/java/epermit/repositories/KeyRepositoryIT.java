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
import epermit.entities.IssuedPermit;
import epermit.entities.Key;
import epermit.models.EPermitProperties;
import epermit.utils.JwsUtil;
import epermit.utils.KeyUtil;
import epermit.utils.PermitUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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
        JwsUtil jwsUtil = new JwsUtil(keyUtil, properties, null);
        PermitUtil permitUtil = new PermitUtil(jwsUtil, null, null);
        Key key = keyUtil.create(ecKey);
        key.setActive(true);
        key.setValidFrom(OffsetDateTime.now(ZoneOffset.UTC).toEpochSecond());
        keyRepository.save(key);
        Optional<Key> keyR = keyRepository.findOneByKeyId("1");
        assertTrue(keyR.isPresent());
        log.info("Private JWk ----------------------------------------------------");
        log.info(keyR.get().getPrivateJwk());
        log.info(keyR.get().getSalt());
        IssuedPermit permit = new IssuedPermit();
        permit.setPermitId("TR-UZ-2021-1-1");
        permit.setIssuedAt("3/6/2021");
        permit.setExpireAt("31/1/2022");
        permit.setPlateNumber("06AA2021");
        permit.setCompanyName("ABC Limited");
        String qrCode = permitUtil.generateQrCode(permit);
        log.info(qrCode);
        // keyUtil.getKey();
    }

}

