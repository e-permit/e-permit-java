package epermit.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import epermit.AppEventListener;
import epermit.PermitPostgresContainer;
import epermit.entities.Authority;
import epermit.entities.LedgerQuota;
import epermit.models.enums.PermitType;
import epermit.models.inputs.CreateQuotaInput;
import epermit.repositories.AuthorityRepository;


@Testcontainers
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
public class AuthorityQuotaControllerIT {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    AuthorityRepository authorityRepository;

    @MockBean
    AppEventListener appEventListener;

    @Transactional
    void setUpAuthority() {
        Authority authority = new Authority();
        authority.setPublicApiUri("apiUri");
        authority.setCode("UZ");
        authority.setName("name");
        LedgerQuota quota = new LedgerQuota();
        quota.setBalance(30L);
        quota.setPermitType(PermitType.BILATERAL);
        quota.setPermitYear(2021);
        authorityRepository.save(authority);
    }

    @BeforeEach
    void setUp(){
        setUpAuthority();
    }

    @Container
    public static PostgreSQLContainer<PermitPostgresContainer> postgreSQLContainer =
            PermitPostgresContainer.getInstance();

    private TestRestTemplate getTestRestTemplate() {
        return testRestTemplate.withBasicAuth("admin", "123456");
    }

    private String getBaseUrl() {
        return "http://localhost:" + port + "/authority_quotas";
    }


    @Test
    void createQuotaTest() {
        CreateQuotaInput input = new CreateQuotaInput();
        input.setAuthorityCode("UZ");
        input.setQuantity(100L);
        input.setPermitType(PermitType.BILATERAL);
        input.setPermitYear(2021);
        ResponseEntity<Void> r =
                getTestRestTemplate().postForEntity(getBaseUrl(), input, Void.class);
        assertEquals(HttpStatus.OK, r.getStatusCode());
    }
}
