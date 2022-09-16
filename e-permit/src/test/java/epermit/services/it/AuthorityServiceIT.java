package epermit.services.it;

import static org.junit.Assert.assertEquals;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
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
import epermit.commons.EpermitValidationException;
import epermit.commons.ErrorCodes;
import epermit.entities.Authority;
import epermit.ledgerevents.LedgerEventUtil;
import epermit.models.EPermitProperties;
import epermit.models.dtos.AuthorityConfig;
import epermit.models.enums.PermitType;
import epermit.models.inputs.CreateAuthorityInput;
import epermit.models.inputs.CreateQuotaInput;
import epermit.repositories.AuthorityRepository;
import epermit.repositories.LedgerPublicKeyRepository;
import epermit.repositories.LedgerQuotaRepository;
import epermit.repositories.PrivateKeyRepository;
import epermit.services.AuthorityService;
import epermit.services.PrivateKeyService;

@Testcontainers
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class AuthorityServiceIT {

    @Autowired
    private AuthorityRepository authorityRepository;

    @Autowired
    private LedgerEventUtil ledgerEventUtil;

    @Autowired
    private LedgerPublicKeyRepository ledgerPublicKeyRepository;

    @Autowired
    private LedgerQuotaRepository ledgerQuotaRepository;

    @Autowired
    private EPermitProperties properties;


    @Container
    public static PostgreSQLContainer<PermitPostgresContainer> postgreSQLContainer =
            PermitPostgresContainer.getInstance();


    @BeforeAll
    @Transactional
    static void up(@Autowired PrivateKeyService privateKeyService, @Autowired PrivateKeyRepository keyRepository) {
        if(keyRepository.count() == 0){
            privateKeyService.seed();
        }    
    }

    @Test
    void createTest() {
        AuthorityService authorityService = new AuthorityService(authorityRepository, properties,
                ledgerEventUtil, ledgerPublicKeyRepository, null, null, new ModelMapper());
        AuthorityConfig config = new AuthorityConfig();
        config.setCode("AZ");
        config.setName("Uz");
        config.setKeys(List.of());
        CreateAuthorityInput input = new CreateAuthorityInput();
        input.setApiUri("apiUri");
        authorityService.create(input, config);
    }


    @Test
    void createQuotaTest() {
        AuthorityService authorityService = new AuthorityService(authorityRepository, properties,
                ledgerEventUtil, ledgerPublicKeyRepository, null, null, new ModelMapper());
        Authority authority = new Authority();
        authority.setApiUri("apiUri");
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
        EpermitValidationException ex =
                Assertions.assertThrows(EpermitValidationException.class, () -> {
                    authorityService.createQuota(input);
                });
        assertEquals(ErrorCodes.INVALID_QUOTA_INTERVAL.name(), ex.getErrorCode());
    }
}

