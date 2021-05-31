package epermit.controllers;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import epermit.PermitPostgresContainer;
import epermit.entities.Key;
import epermit.models.dtos.AuthorityConfig;
import epermit.repositories.KeyRepository;
import epermit.services.KeyService;
import epermit.utils.KeyUtil;

@Testcontainers
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class ConfigControllerIT {
    @LocalServerPort
    private int port;

    @Autowired
    private KeyUtil keyUtil;

    @Autowired
    private KeyRepository keyRepository;

    @Autowired
    private TestRestTemplate restTemplate;

    @Container
    public static PostgreSQLContainer<PermitPostgresContainer> postgreSQLContainer =
            PermitPostgresContainer.getInstance();


    @BeforeEach
    @Transactional
    void setUp() {
        Key key = keyUtil.create("1");
        key.setEnabled(true);
        keyRepository.save(key);
    }

    @Test
    void getTest() {
        final String baseUrl = "http://localhost:" + port + "/epermit-configuration";
        ResponseEntity<AuthorityConfig> result =
                restTemplate.getForEntity(baseUrl, AuthorityConfig.class);
        Assert.assertEquals(200, result.getStatusCodeValue());
        Assert.assertEquals("TR", result.getBody().getCode()); 
               
    }

}
