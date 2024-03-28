package epermit.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import epermit.PermitPostgresContainer;
import epermit.entities.Authority;
import epermit.models.dtos.AuthorityDto;
import epermit.models.inputs.CreateAuthorityInput;
import epermit.repositories.AuthorityRepository;

@Testcontainers
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
public class AuthorityControllerIT {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    AuthorityRepository authorityRepository;

    @Container
    public static PostgreSQLContainer<PermitPostgresContainer> postgreSQLContainer = PermitPostgresContainer
            .getInstance();

    private TestRestTemplate getTestRestTemplate() {
        return testRestTemplate.withBasicAuth("admin", "123456");
    }

    private String getBaseUrl() {
        return "http://localhost:" + port + "/authorities";
    }

    @Test
    void getAllTest() {
        Authority authority = new Authority();
        authority.setPublicApiUri("apiUri");
        authority.setCode("UZ");
        authority.setName("name");
        authorityRepository.save(authority);
        ResponseEntity<AuthorityDto[]> r = getTestRestTemplate().getForEntity(getBaseUrl(), AuthorityDto[].class);
        assertEquals(HttpStatus.OK, r.getStatusCode());
        var body = r.getBody();
        Assert.assertNotNull("Null body", body);
        assertEquals(1, body.length);

    }

    @Test
    void getByCodeTest() {
        Authority authority = new Authority();
        authority.setPublicApiUri("apiUri");
        authority.setCode("UZ");
        authority.setName("name");
        authorityRepository.save(authority);
        ResponseEntity<AuthorityDto> r = getTestRestTemplate().getForEntity(getBaseUrl() + "/UZ", AuthorityDto.class);
        assertEquals(HttpStatus.OK, r.getStatusCode());
        var body = r.getBody();
        Assert.assertNotNull("", body);
        assertEquals("UZ", body.getCode());
        assertEquals("apiUri", body.getApiUri());
        assertEquals("name", body.getName());
    }

    @Test
    void createTest() {
        CreateAuthorityInput input = new CreateAuthorityInput();
        input.setPublicApiUri("http://localhost");
        input.setCode("UZ");
        input.setName("Uzbekistan");
        ResponseEntity<?> r = getTestRestTemplate().postForEntity(getBaseUrl(), input, String.class);
        assertEquals(HttpStatus.OK, r.getStatusCode());
        AuthorityDto authority = getTestRestTemplate().getForObject(getBaseUrl() + "/UZ", AuthorityDto.class);
        assertEquals("UZ", authority.getCode());
    }
}
