package epermit.controllers;


import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import epermit.PermitPostgresContainer;
import epermit.RestResponsePage;
import epermit.entities.Authority;
import epermit.entities.LedgerPermit;
import epermit.entities.PrivateKey;
import epermit.models.dtos.PermitDto;
import epermit.models.enums.PermitActivityType;
import epermit.models.enums.PermitType;
import epermit.models.inputs.PermitUsedInput;
import epermit.repositories.AuthorityRepository;
import epermit.repositories.LedgerPermitRepository;
import epermit.repositories.PrivateKeyRepository;
import epermit.utils.PrivateKeyUtil;


@Testcontainers
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class PermitControllerIT {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    AuthorityRepository authorityRepository;

    @Autowired
    PrivateKeyRepository keyRepository;

    @Autowired
    LedgerPermitRepository permitRepository;

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
        return "http://localhost:" + port + "/permits";
    }

    @Test
    void getAllTest() {
        for (int i = 0; i < 25; i++) {
            LedgerPermit permit = new LedgerPermit();
            permit.setCompanyName("ABC");
            permit.setIssuer("UZ");
            permit.setPermitType(PermitType.BILITERAL);
            permit.setPermitYear(2021);
            permit.setPlateNumber("06AA1234");
            permit.setExpireAt("31/01/2022");
            permit.setIssuedAt("03/03/2021");
            permit.setPermitId("ABC");
            permit.setSerialNumber(1);
            permitRepository.save(permit);
        }
        ParameterizedTypeReference<RestResponsePage<PermitDto>> responseType =
                new ParameterizedTypeReference<RestResponsePage<PermitDto>>() {};
        ResponseEntity<RestResponsePage<PermitDto>> result =
                getTestRestTemplate().exchange(getBaseUrl(), HttpMethod.GET, null, responseType);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(25, result.getBody().getTotalElements());

    }

    @Test
    void getByIdTest() {
        LedgerPermit permit = new LedgerPermit();
        permit.setCompanyName("ABC");
        permit.setIssuer("UZ");
        permit.setPermitType(PermitType.BILITERAL);
        permit.setPermitYear(2021);
        permit.setPlateNumber("06AA1234");
        permit.setExpireAt("31/01/2022");
        permit.setIssuedAt("03/03/2021");
        permit.setPermitId("ABC");
        permit.setSerialNumber(1);
        permitRepository.save(permit);
        PermitDto dto = getTestRestTemplate().getForObject(getBaseUrl() + "/" + permit.getId(),
                PermitDto.class);
        assertEquals("ABC", dto.getPermitId());
    }

    @Test
    void usePermitTest() {
        LedgerPermit permit = new LedgerPermit();
        permit.setCompanyName("ABC");
        permit.setIssuer("UZ");
        permit.setPermitType(PermitType.BILITERAL);
        permit.setPermitYear(2021);
        permit.setPlateNumber("06AA1234");
        permit.setExpireAt("31/01/2022");
        permit.setIssuedAt("03/03/2021");
        permit.setPermitId("ABC");
        permit.setSerialNumber(1);
        permitRepository.save(permit);
        PermitUsedInput input = new PermitUsedInput();
        input.setActivityType(PermitActivityType.ENTRANCE);
        ResponseEntity<Void> r = getTestRestTemplate().postForEntity(
                getBaseUrl() + "/" + permit.getPermitId() + "/activities", input, Void.class);
        assertEquals(HttpStatus.OK, r.getStatusCode());
    }
}

