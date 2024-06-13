package epermit.services.it;

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
import epermit.entities.LedgerPermit;
import epermit.entities.LedgerQuota;
import epermit.models.dtos.AuthorityConfig;
import epermit.models.inputs.CreateAuthorityInput;
import epermit.models.inputs.CreateQuotaInput;
import epermit.repositories.AuthorityRepository;
import epermit.repositories.LedgerPermitRepository;
import epermit.repositories.LedgerQuotaRepository;
import epermit.services.AuthorityService;
import epermit.services.KeyService;

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

    @BeforeAll
    @Transactional
    static void up(@Autowired KeyService keyService) {
        keyService.seed();
    }

    @Test
    void getByCodeTest() {
        CreateAuthorityInput input = new CreateAuthorityInput();
        input.setCode("AY");
        input.setName("Ay");
        input.setPublicApiUri("apiUri");
        authorityService.create(input, new AuthorityConfig());
        LedgerQuota quota = new LedgerQuota();
        quota.setPermitIssuedFor("AY");
        quota.setPermitIssuer("A");
        quota.setPermitType(1);
        quota.setPermitYear(2022);
        quota.setBalance(100L);
        ledgerQuotaRepository.save(quota);
        LedgerPermit permit = new LedgerPermit();
        permit.setCompanyId("companyId");
        permit.setCompanyName("companyId");
        permit.setExpireAt("companyId");
        permit.setIssuedAt("companyId");
        permit.setIssuedFor("AY");
        permit.setIssuer("A");
        permit.setPermitId("1");
        permit.setPermitType(1);
        permit.setPermitYear(2022);
        permit.setPlateNumber("companyId");
        permit.setArrivalCountry("A");
        permit.setDepartureCountry("AY");
        permit.setQrCode("A");
        permit.setUsed(false);
        ledgerPermitRepository.save(permit);
    }

    @Test
    void createTest() {
        CreateAuthorityInput input = new CreateAuthorityInput();
        input.setCode("C");
        input.setName("C");
        input.setPublicApiUri("apiUri");
        authorityService.create(input, new AuthorityConfig());
    }

    @Test
    void createQuotaTest() {
        Authority authority = new Authority();
        authority.setPublicApiUri("apiUri");
        authority.setCode("B");
        authority.setName("B");
        authorityRepository.save(authority);
        CreateQuotaInput input = new CreateQuotaInput();
        input.setQuantity(20L);
        input.setPermitType(1);
        input.setPermitYear(2021);
        authorityService.createQuota("B", input);
    }
}
