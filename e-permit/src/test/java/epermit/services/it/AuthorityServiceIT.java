package epermit.services.it;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import epermit.PermitPostgresContainer;
import epermit.entities.Authority;
import epermit.entities.LedgerPermit;
import epermit.entities.LedgerQuota;
import epermit.models.enums.PermitType;
import epermit.models.inputs.CreateAuthorityInput;
import epermit.models.inputs.CreateQuotaInput;
import epermit.repositories.AuthorityRepository;
import epermit.repositories.LedgerPermitRepository;
import epermit.repositories.LedgerQuotaRepository;
import epermit.services.AuthorityService;

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
    public static PostgreSQLContainer<PermitPostgresContainer> postgreSQLContainer = PermitPostgresContainer
            .getInstance();

    @Test
    void getByCodeTest() {

        CreateAuthorityInput input = new CreateAuthorityInput();
        input.setCode("AY");
        input.setName("Ay");
        input.setApiUri("apiUri");
        authorityService.create(input);
        LedgerQuota quota = new LedgerQuota();
        quota.setPermitIssuedFor("AY");
        quota.setPermitIssuer("TR");
        quota.setPermitType(PermitType.BILITERAL);
        quota.setPermitYear(2022);
        quota.setBalance(100L);
        ledgerQuotaRepository.save(quota);
        LedgerPermit permit = new LedgerPermit();
        permit.setCompanyId("companyId");
        permit.setCompanyName("companyId");
        permit.setExpireAt("companyId");
        permit.setIssuedAt("companyId");
        permit.setIssuedFor("AY");
        permit.setIssuer("TR");
        permit.setPermitId("1");
        permit.setPermitType(PermitType.BILITERAL);
        permit.setPermitYear(2022);
        permit.setPlateNumber("companyId");
        permit.setUsed(false);
        permit.setSerialNumber(100L);
        ledgerPermitRepository.save(permit);
    }

    @Test
    void createTest() {
        CreateAuthorityInput input = new CreateAuthorityInput();
        input.setCode("AZ");
        input.setName("Uz");
        input.setApiUri("apiUri");
        authorityService.create(input);
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
        input.setQuantity(20L);
        input.setPermitType(PermitType.BILITERAL);
        input.setPermitYear(2021);
        authorityService.createQuota(input);
        authorityService.createQuota(input);
    }
}
