package epermit.controllers;


import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.Map;
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
import epermit.entities.PrivateKey;
import epermit.repositories.AuthorityRepository;
import epermit.repositories.PrivateKeyRepository;
import epermit.utils.PrivateKeyUtil;


@Testcontainers
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class KeyControllerIT {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    AuthorityRepository authorityRepository;

    @Autowired
    PrivateKeyRepository keyRepository;

    @Autowired
    PrivateKeyUtil keyUtil;

    @BeforeEach
    @Transactional
    void setUp() {
        Authority authority = new Authority();
        authority.setApiUri("apiUri");
        authority.setCode("UZ");
        authority.setName("name");
        authorityRepository.save(authority);
        epermit.models.dtos.PrivateKey key = keyUtil.create("1");
        PrivateKey keyEntity = new PrivateKey();
        keyEntity.setKeyId(key.getKeyId());
        keyEntity.setPrivateJwk(key.getPrivateJwk());
        keyEntity.setEnabled(true);
        keyRepository.save(keyEntity);
    }

    @Container
    public static PostgreSQLContainer<PermitPostgresContainer> postgreSQLContainer =
            PermitPostgresContainer.getInstance();

    private TestRestTemplate getTestRestTemplate() {
        return testRestTemplate.withBasicAuth("admin", "123456");
    }

    private String getBaseUrl() {
        return "http://localhost:" + port + "/keys";
    }


    @Test
    void createTest() {
        Map<String, String> input = Map.of("key_id", "2");
        ResponseEntity<Void> r =
                getTestRestTemplate().postForEntity(getBaseUrl(), input, Void.class);
        assertEquals(HttpStatus.OK, r.getStatusCode());
    }

    @Test
    void enableTest() {
        epermit.models.dtos.PrivateKey key = keyUtil.create("2");
        PrivateKey keyEntity = new PrivateKey();
        keyEntity.setKeyId(key.getKeyId());
        keyEntity.setPrivateJwk(key.getPrivateJwk());
        keyRepository.save(keyEntity);
        RestTemplate restTemplate = getTestRestTemplate().getRestTemplate();
        HttpComponentsClientHttpRequestFactory requestFactory =
                new HttpComponentsClientHttpRequestFactory();
        requestFactory.setConnectTimeout(0);
        requestFactory.setReadTimeout(0);
        restTemplate.setRequestFactory(requestFactory);
        HttpEntity<String> entity = new HttpEntity<String>("{}");
        ResponseEntity<Void> r = restTemplate.exchange(getBaseUrl() + "/" + keyEntity.getId() + "/enable",
                HttpMethod.PATCH, entity, Void.class);
        assertEquals(HttpStatus.OK, r.getStatusCode());
    }

    @Test
    void revokeTest() {
        epermit.models.dtos.PrivateKey key = keyUtil.create("2");
        PrivateKey keyEntity = new PrivateKey();
        keyEntity.setKeyId(key.getKeyId());
        keyEntity.setPrivateJwk(key.getPrivateJwk());
        keyEntity.setEnabled(true);
        keyRepository.save(keyEntity);
        HttpEntity<String> entity = new HttpEntity<String>("{}");
        ResponseEntity<Void> r = getTestRestTemplate().exchange(getBaseUrl() + "/" + keyEntity.getId(),
                HttpMethod.DELETE, entity, Void.class);
        assertEquals(HttpStatus.OK, r.getStatusCode());
    }
}

