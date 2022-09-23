package epermit.services.it;

import static org.junit.Assert.assertEquals;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import epermit.PermitPostgresContainer;
import epermit.entities.Authority;
import epermit.entities.LedgerPublicKey;
import epermit.models.EPermitProperties;
import epermit.models.dtos.AuthorityConfig;
import epermit.models.dtos.TrustedAuthority;
import epermit.repositories.AuthorityRepository;
import epermit.repositories.LedgerPublicKeyRepository;
import epermit.repositories.PrivateKeyRepository;
import epermit.services.ConfigService;
import epermit.services.PrivateKeyService;

@Testcontainers
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class ConfigServiceIT {

    private String jwk =
            "{\"kty\":\"EC\",\"crv\":\"P-256\",\"x\":\"uWFoZ2J2BdSP-eCkqpNO2H4DoXeFNWEWrPiQ09hMJg8\",\"y\":\"FDqdZirvBlV_Au_4971Gd6d92_Z8abzSijr5a64vc9o\",\"use\":\"sig\",\"kid\":\"1\",\"alg\":\"ES256\"}";


    @Autowired
    private AuthorityRepository authorityRepository;

    @Autowired
    private LedgerPublicKeyRepository ledgerPublicKeyRepository;

    @Autowired
    private EPermitProperties properties;


    @Container
    public static PostgreSQLContainer<PermitPostgresContainer> postgreSQLContainer =
            PermitPostgresContainer.getInstance();


    @BeforeAll
    @Transactional
    static void up(@Autowired PrivateKeyService privateKeyService,
            @Autowired PrivateKeyRepository keyRepository) {
        if (keyRepository.count() == 0) {
            privateKeyService.seed();
        }
    }

    @Test
    void getConfigTest() {
        ConfigService configService =
                new ConfigService(authorityRepository, properties, ledgerPublicKeyRepository);
        AuthorityConfig config = configService.getConfig();
        assertEquals("TR", config.getCode());
        assertEquals(1, config.getKeys().size());
    }

    @Test
    void getTrustedAuthoritiesTest() {
        seedAuthorities();
        ConfigService configService =
                new ConfigService(authorityRepository, properties, ledgerPublicKeyRepository);
        List<TrustedAuthority> authorities = configService.getTrustedAuthorities();
        assertEquals(1, authorities.size());
    }

    @Transactional
    void seedAuthorities() {
        authorityRepository.deleteAll();
        Authority authority = new Authority();
        authority.setApiUri("apiUri");
        authority.setCode("RU");
        authority.setName("Uz");
        authorityRepository.save(authority);
        LedgerPublicKey publicKey = new LedgerPublicKey();
        publicKey.setJwk(jwk);
        publicKey.setKeyId("1");
        publicKey.setAuthorityCode("RU");
        ledgerPublicKeyRepository.save(publicKey);
    }
}


