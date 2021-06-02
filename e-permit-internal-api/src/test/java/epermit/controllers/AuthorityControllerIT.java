package epermit.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import epermit.PermitPostgresContainer;
import epermit.entities.Authority;
import epermit.models.dtos.AuthorityDto;
import epermit.repositories.AuthorityRepository;


@Testcontainers
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class AuthorityControllerIT {

    @LocalServerPort
    private int port;

    final String baseUrl = "http://localhost:";

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    AuthorityRepository authorityRepository;

    @Container
    public static PostgreSQLContainer<PermitPostgresContainer> postgreSQLContainer =
            PermitPostgresContainer.getInstance();

    
    @Test
    @WithMockUser()
    void getAllTest() {
        Authority authority = new Authority();
        authority.setApiUri("apiUri");
        authority.setCode("UA");
        authority.setName("name");
        authority.setVerifyUri("verifyUri");
        authorityRepository.save(authority);
        ResponseEntity<AuthorityDto[]> r = restTemplate.withBasicAuth("admin", "123456")
                .getForEntity(baseUrl + port + "/authorities", AuthorityDto[].class);
        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertEquals(1, r.getBody().length);
    }
}
