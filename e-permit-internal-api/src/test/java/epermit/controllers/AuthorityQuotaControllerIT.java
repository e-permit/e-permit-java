package epermit.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import epermit.PermitPostgresContainer;
import epermit.entities.Authority;
import epermit.entities.Key;
import epermit.entities.VerifierQuota;
import epermit.models.enums.PermitType;
import epermit.models.inputs.CreateQuotaInput;
import epermit.repositories.AuthorityRepository;
import epermit.repositories.KeyRepository;
import epermit.utils.KeyUtil;


@Testcontainers
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class AuthorityQuotaControllerIT {

    @LocalServerPort
    private int port;

    private int quotaId;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    AuthorityRepository authorityRepository;

    @Autowired
    KeyRepository keyRepository;

    @Autowired
    KeyUtil keyUtil;

    @Transactional
    void setUpAuthority() {
        Authority authority = new Authority();
        authority.setApiUri("apiUri");
        authority.setCode("UZ");
        authority.setName("name");
        authority.setVerifyUri("verifyUri");
        VerifierQuota quota = new VerifierQuota();
        quota.setEndNumber(30);
        quota.setStartNumber(1);
        quota.setPermitType(PermitType.BILITERAL);
        quota.setPermitYear(2021);
        authority.addVerifierQuota(quota);
        authorityRepository.save(authority);
        Key key = keyUtil.create("1");
        key.setEnabled(true);
        keyRepository.save(key);
        quotaId = quota.getId();
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
        input.setEndId(100);
        input.setPermitType(PermitType.BILITERAL);
        input.setPermitYear(2021);
        input.setStartId(1);
        ResponseEntity<Void> r =
                getTestRestTemplate().postForEntity(getBaseUrl(), input, Void.class);
        assertEquals(HttpStatus.OK, r.getStatusCode());
    }

    @Test
    void enableQuotaTest() {
        RestTemplate restTemplate = getTestRestTemplate().getRestTemplate();
        HttpComponentsClientHttpRequestFactory requestFactory =
                new HttpComponentsClientHttpRequestFactory();
        requestFactory.setConnectTimeout(0);
        requestFactory.setReadTimeout(0);
        restTemplate.setRequestFactory(requestFactory);
        HttpEntity<String> entity = new HttpEntity<String>("{}");
        ResponseEntity<Void> r = restTemplate.exchange(getBaseUrl() + "/" + quotaId + "/enable",
                HttpMethod.PATCH, entity, Void.class);
        assertEquals(HttpStatus.OK, r.getStatusCode());
    }
}
