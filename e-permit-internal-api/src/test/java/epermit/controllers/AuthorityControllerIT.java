package epermit.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import epermit.PermitPostgresContainer;
import epermit.commons.GsonUtil;
import epermit.entities.Authority;
import epermit.models.dtos.AuthorityConfig;
import epermit.models.dtos.AuthorityDto;
import epermit.models.dtos.PublicJwk;
import epermit.models.inputs.CreateAuthorityInput;
import epermit.repositories.AuthorityRepository;


@Testcontainers
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class AuthorityControllerIT {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    AuthorityRepository authorityRepository;

    private MockRestServiceServer mockServer;

    @BeforeEach
    void setUp() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Container
    public static PostgreSQLContainer<PermitPostgresContainer> postgreSQLContainer =
            PermitPostgresContainer.getInstance();

    private TestRestTemplate getTestRestTemplate() {
        return testRestTemplate.withBasicAuth("admin", "123456");
    }

    private String getBaseUrl() {
        return "http://localhost:" + port + "/authorities";
    }


    @Test
    void getAllTest() {
        Authority authority = new Authority();
        authority.setApiUri("apiUri");
        authority.setCode("UZ");
        authority.setName("name");
        authorityRepository.save(authority);
        ResponseEntity<AuthorityDto[]> r =
                getTestRestTemplate().getForEntity(getBaseUrl(), AuthorityDto[].class);
        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertEquals(1, r.getBody().length);
    }

    @Test
    void getByCodeTest() {
        Authority authority = new Authority();
        authority.setApiUri("apiUri");
        authority.setCode("UZ");
        authority.setName("name");
        authorityRepository.save(authority);
        ResponseEntity<AuthorityDto> r =
                getTestRestTemplate().getForEntity(getBaseUrl() + "/UZ", AuthorityDto.class);
        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertEquals("UZ", r.getBody().getCode());
        assertEquals("apiUri", r.getBody().getApiUri());
        assertEquals("name", r.getBody().getName());
        assertEquals("verifyUri", r.getBody().getVerifyUri());
    }

    @Test
    void createTest() {
        AuthorityConfig config = new AuthorityConfig();
        config.setCode("UZ");
        PublicJwk jwk = new PublicJwk();
        jwk.setCrv("crv");
        jwk.setKid("1");
        jwk.setKty("kty");
        jwk.setUse("sig");
        jwk.setX("x");
        jwk.setY("y");
        config.setKeys(List.of(jwk));
        //config.setTrustedAuthorities(List.of());
        mockServer.expect(once(), requestTo("http://localhost/epermit-configuration")).andRespond(
                withSuccess(GsonUtil.getGson().toJson(config), MediaType.APPLICATION_JSON));

        CreateAuthorityInput input = new CreateAuthorityInput();
        input.setApiUri("http://localhost");
        ResponseEntity<Void> r =
                getTestRestTemplate().postForEntity(getBaseUrl(), input, Void.class);
        assertEquals(HttpStatus.OK, r.getStatusCode());
        AuthorityDto authority = getTestRestTemplate().getForObject(getBaseUrl() + "/UZ", AuthorityDto.class);
        assertEquals(1, authority.getKeys().size());
    }
}
