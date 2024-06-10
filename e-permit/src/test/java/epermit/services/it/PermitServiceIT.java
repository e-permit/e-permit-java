package epermit.services.it;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import epermit.PermitPostgresContainer;
import epermit.entities.Authority;
import epermit.entities.LedgerPermit;
import epermit.entities.LedgerQuota;
import epermit.models.inputs.CreatePermitInput;
import epermit.models.results.CreatePermitResult;
import epermit.repositories.AuthorityRepository;
import epermit.repositories.LedgerPermitRepository;
import epermit.repositories.LedgerQuotaRepository;
import epermit.services.KeyService;
import epermit.services.PermitService;

@Testcontainers
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class PermitServiceIT {
    @Autowired
    private PermitService permitService;

    @Autowired
    private LedgerPermitRepository ledgerPermitRepository;

    @MockBean
    RestTemplate restTemplate;

    @Container
    public static PostgreSQLContainer<PermitPostgresContainer> postgreSQLContainer = PermitPostgresContainer
            .getInstance();

    @BeforeAll
    @Transactional
    static void up(@Autowired KeyService keyService) {
        keyService.seed();
    }

    @BeforeAll
    @Transactional
    static void setUp(@Autowired AuthorityRepository authorityRepository,
            @Autowired LedgerQuotaRepository ledgerQuotaRepository) {
        Authority authority = new Authority();
        authority.setPublicApiUri("apiUri");
        authority.setCode("FR");
        authority.setName("Uz");
        authorityRepository.save(authority);

        LedgerQuota quota = new LedgerQuota();
        quota.setBalance(100L);
        quota.setPermitIssuedFor("FR");
        quota.setPermitIssuer("TR");
        quota.setPermitType(1);
        quota.setPermitYear(2021);
        ledgerQuotaRepository.save(quota);
    }

    @Test
    void permitCreatedTest() {
        CreatePermitInput input = new CreatePermitInput();
        input.setCompanyId("ABC");
        input.setCompanyName("ABC");
        input.setIssuedFor("FR");
        input.setPermitType(1);
        input.setPermitYear(2021);
        input.setPlateNumber("ABC");
        input.setArrivalCountry("FR");
        permitService.createPermit(input);
    }

    @Test
    void permitRevokedTest() {
        CreatePermitInput input = new CreatePermitInput();
        input.setCompanyId("ABC");
        input.setCompanyName("ABC");
        input.setIssuedFor("FR");
        input.setPermitType(1);
        input.setPermitYear(2021);
        input.setPlateNumber("ABC");
        input.setArrivalCountry("FR");
        CreatePermitResult r = permitService.createPermit(input);
        Optional<LedgerPermit> permit = ledgerPermitRepository.findOneByPermitId(r.getPermitId());
        Assertions.assertTrue(permit.isPresent());
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), any()))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));
        permitService.revokePermit(r.getPermitId());
        Optional<LedgerPermit> permit2 = ledgerPermitRepository.findOneByPermitId(r.getPermitId());
        Assertions.assertTrue(permit2.isPresent());
        Assertions.assertTrue(permit2.get().isRevoked());

    }
}
