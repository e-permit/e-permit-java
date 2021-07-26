package epermit.services;


import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
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
import epermit.ledgerevents.LedgerEventUtil;
import epermit.models.EPermitProperties;
import epermit.models.dtos.AuthorityConfig;
import epermit.models.dtos.PrivateKey;
import epermit.models.enums.AuthenticationType;
import epermit.models.enums.PermitType;
import epermit.models.inputs.CreateAuthorityInput;
import epermit.models.inputs.CreateQuotaInput;
import epermit.repositories.AuthorityRepository;
import epermit.repositories.LedgerPublicKeyRepository;
import epermit.repositories.PrivateKeyRepository;
import epermit.utils.PrivateKeyUtil;

@Testcontainers
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class AuthorityServiceIT {

    @Autowired
    private AuthorityRepository authorityRepository;

    @Autowired
    private PrivateKeyRepository keyRepository;

    @Autowired
    private PrivateKeyUtil keyUtil;

    @Autowired
    private LedgerEventUtil ledgerEventUtil;

    @Autowired
    private LedgerPublicKeyRepository ledgerPublicKeyRepository;

    @Autowired
    private EPermitProperties properties;


    @Container
    public static PostgreSQLContainer<PermitPostgresContainer> postgreSQLContainer =
            PermitPostgresContainer.getInstance();


    @BeforeEach
    @Transactional
    void setUp() {
        PrivateKey key = keyUtil.create("1");
        epermit.entities.PrivateKey keyEntity = new epermit.entities.PrivateKey();
        keyEntity.setEnabled(true);
        keyEntity.setKeyId(key.getKeyId());
        keyEntity.setPrivateJwk(key.getPrivateJwk());
        keyEntity.setSalt(key.getSalt());
        keyRepository.save(keyEntity);
    }

    @Test
    void createTest() {
        AuthorityService authorityService = new AuthorityService(authorityRepository, properties,
                ledgerEventUtil, ledgerPublicKeyRepository, new ModelMapper());
        AuthorityConfig config = new AuthorityConfig();
        config.setCode("UZ");
        config.setName("Uzbekistan");
        config.setKeys(List.of());
        CreateAuthorityInput input = new CreateAuthorityInput();
        input.setApiUri("apiUri");
        authorityService.create(input, config);
    }

    
    @Test
    void createQuotaTest() {
        AuthorityService authorityService = new AuthorityService(authorityRepository, properties,
                ledgerEventUtil, ledgerPublicKeyRepository, new ModelMapper());
        Authority authority = new Authority();
        authority.setApiUri("apiUri");
        authority.setAuthenticationType(AuthenticationType.BASIC);
        authority.setCode("UZ");
        authority.setName("Uz");
        authorityRepository.save(authority);
        CreateQuotaInput input = new CreateQuotaInput();
        input.setAuthorityCode("UZ");
        input.setEndNumber(20);
        input.setPermitType(PermitType.BILITERAL);
        input.setPermitYear(2021);
        input.setStartNumber(1);
        authorityService.createQuota(input);
        authorityService.createQuota(input);
    }
}

