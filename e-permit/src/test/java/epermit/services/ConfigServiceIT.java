package epermit.services;

import static org.junit.Assert.assertEquals;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
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
import epermit.commons.GsonUtil;
import epermit.entities.Authority;
import epermit.entities.LedgerPublicKey;
import epermit.models.EPermitProperties;
import epermit.models.dtos.AuthorityConfig;
import epermit.models.dtos.TrustedAuthority;
import epermit.models.enums.AuthenticationType;
import epermit.repositories.AuthorityRepository;
import epermit.repositories.LedgerPublicKeyRepository;

@Testcontainers
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class ConfigServiceIT {

    private String jwk =
            "{\"kty\":\"EC\",\"crv\":\"P-256\",\"x\":\"uWFoZ2J2BdSP-eCkqpNO2H4DoXeFNWEWrPiQ09hMJg8\",\"y\":\"FDqdZirvBlV_Au_4971Gd6d92_Z8abzSijr5a64vc9o\",\"use\":\"sig\",\"kid\":\"1\",\"alg\":\"ES256\"}";


    @Autowired
    private AuthorityRepository authorityRepository;

    @Autowired
    private PrivateKeyService privateKeyService;

    @Autowired
    private LedgerPublicKeyRepository ledgerPublicKeyRepository;

    @Autowired
    private EPermitProperties properties;


    @Container
    public static PostgreSQLContainer<PermitPostgresContainer> postgreSQLContainer =
            PermitPostgresContainer.getInstance();


    @BeforeEach
    void setUp() {
        privateKeyService.seed();
    }

    @Test
    void getConfigTest() {
        ConfigService configService = new ConfigService(authorityRepository, properties, ledgerPublicKeyRepository);
        AuthorityConfig config = configService.getConfig();
        assertEquals("TR", config.getCode());
        assertEquals(1, config.getKeys().size());
    }

    @Test
    void getTrustedAuthoritiesTest() { 
        seedAuthorities();
        ConfigService configService = new ConfigService(authorityRepository, properties, ledgerPublicKeyRepository);
        List<TrustedAuthority> authorities = configService.getTrustedAuthorities();
        assertEquals(1, authorities.size());
    }

    @Transactional
    void seedAuthorities(){
        Authority authority = new Authority();
        authority.setApiUri("apiUri");
        authority.setAuthenticationType(AuthenticationType.BASIC);
        authority.setCode("UZ");
        authority.setName("Uz");
        authorityRepository.save(authority);
        LedgerPublicKey publicKey = new LedgerPublicKey();
        publicKey.setJwk(jwk);
        publicKey.setKeyId("1");
        publicKey.setAuthorityCode("UZ");
        ledgerPublicKeyRepository.save(publicKey);
    }
}


