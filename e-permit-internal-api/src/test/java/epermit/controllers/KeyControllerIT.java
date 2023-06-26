package epermit.controllers;


import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import epermit.AppEventListener;
import epermit.PermitPostgresContainer;
import epermit.entities.Authority;
import epermit.entities.LedgerPublicKey;
import epermit.entities.PrivateKey;
import epermit.repositories.AuthorityRepository;
import epermit.repositories.LedgerPublicKeyRepository;
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
    LedgerPublicKeyRepository ledgerPublicKeyRepository;

    @Autowired
    PrivateKeyRepository keyRepository;

    @Autowired
    PrivateKeyUtil keyUtil;

    @MockBean
    AppEventListener appEventListener;

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
        keyEntity.setSalt(key.getSalt());
        keyEntity.setEnabled(true);
        keyRepository.save(keyEntity);
        LedgerPublicKey pubKey = new LedgerPublicKey();
        pubKey.setAuthorityCode("TR");
        pubKey.setJwk(key.getPublicJwk());
        pubKey.setKeyId("1");
        ledgerPublicKeyRepository.save(pubKey);
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
    void revokeTest() {
        epermit.models.dtos.PrivateKey key = keyUtil.create("2");
        PrivateKey keyEntity = new PrivateKey();
        keyEntity.setKeyId(key.getKeyId());
        keyEntity.setPrivateJwk(key.getPrivateJwk());
        keyEntity.setSalt(key.getSalt());
        keyEntity.setEnabled(true);
        keyRepository.save(keyEntity);
        LedgerPublicKey pubKey = new LedgerPublicKey();
        pubKey.setAuthorityCode("TR");
        pubKey.setJwk(key.getPublicJwk());
        pubKey.setKeyId("2");
        ledgerPublicKeyRepository.save(pubKey);
        HttpEntity<String> entity = new HttpEntity<String>("{}");
        ResponseEntity<?> r = getTestRestTemplate().exchange(getBaseUrl() + "/" + keyEntity.getId(),
                HttpMethod.DELETE, entity, String.class);
        assertEquals(HttpStatus.OK, r.getStatusCode());
    }
}

