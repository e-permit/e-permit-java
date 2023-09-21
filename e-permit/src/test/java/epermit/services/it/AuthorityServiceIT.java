package epermit.services.it;

import java.util.List;
import org.junit.jupiter.api.Assertions;
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
import epermit.commons.EpermitValidationException;
import epermit.commons.ErrorCodes;
import epermit.entities.Authority;
import epermit.entities.LedgerPermit;
import epermit.entities.LedgerQuota;
import epermit.models.dtos.AuthorityConfig;
import epermit.models.dtos.AuthorityDto;
import epermit.models.enums.PermitType;
import epermit.models.inputs.CreateAuthorityInput;
import epermit.models.inputs.CreateQuotaInput;
import epermit.repositories.AuthorityRepository;
import epermit.repositories.LedgerPermitRepository;
import epermit.repositories.LedgerQuotaRepository;
import epermit.repositories.PrivateKeyRepository;
import epermit.services.AuthorityService;
import epermit.services.PrivateKeyService;

@Testcontainers
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class AuthorityServiceIT {

    @Autowired
    private AuthorityService authorityService;

    @Autowired
    private AuthorityRepository authorityRepository;

    @Autowired
    private LedgerQuotaRepository ledgerQuotaRepository;

    @Autowired
    private LedgerPermitRepository ledgerPermitRepository;


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
    void getByCodeTest(){
        AuthorityConfig config = new AuthorityConfig();
        config.setCode("AY");
        config.setName("Ay");
        config.setKeys(List.of());
        CreateAuthorityInput input = new CreateAuthorityInput();
        input.setApiUri("apiUri");
        authorityService.create(input, config);
        LedgerQuota quota = new LedgerQuota();
        quota.setActive(true);
        quota.setPermitIssuedFor("AY");
        quota.setPermitIssuer("TR");
        quota.setPermitType(PermitType.BILATERAL);
        quota.setPermitYear(2022);
        quota.setStartNumber(1);
        quota.setEndNumber(100);
        ledgerQuotaRepository.save(quota);
        LedgerPermit permit = new LedgerPermit();
        permit.setCompanyId("companyId");
        permit.setCompanyName("companyId");
        permit.setExpireAt("companyId");
        permit.setIssuedAt("companyId");
        permit.setIssuedFor("AY");
        permit.setIssuer("TR");
        permit.setPermitId("1");
        permit.setPermitType(PermitType.BILATERAL);
        permit.setPermitYear(2022);
        permit.setPlateNumber("companyId");
        permit.setQrCode("companyId");
        permit.setUsed(false);
        permit.setSerialNumber(100);
        ledgerPermitRepository.save(permit);
        AuthorityDto dto = authorityService.getByCode("AY");
        Assertions.assertEquals(dto.getQuotas().get(0).getUsedCount(), 1L);
    }
    @Test
    void createTest() {
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
        Authority authority = new Authority();
        authority.setApiUri("apiUri");
        authority.setCode("UZ");
        authority.setName("Uz");
        authorityRepository.save(authority);
        CreateQuotaInput input = new CreateQuotaInput();
        input.setAuthorityCode("UZ");
        input.setEndNumber(20);
        input.setPermitType(PermitType.BILATERAL);
        input.setPermitYear(2021);
        input.setStartNumber(1);
        authorityService.createQuota(input);
 
        EpermitValidationException ex =
                Assertions.assertThrows(EpermitValidationException.class, () -> {
                    authorityService.createQuota(input);
                });
        Assertions.assertEquals(ErrorCodes.INVALID_QUOTA_INTERVAL.name(), ex.getErrorCode());
    }
}

