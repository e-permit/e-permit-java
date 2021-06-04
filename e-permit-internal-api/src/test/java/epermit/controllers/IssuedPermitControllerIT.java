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
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import epermit.PermitPostgresContainer;
import epermit.RestResponsePage;
import epermit.entities.Authority;
import epermit.entities.IssuedPermit;
import epermit.entities.IssuerQuota;
import epermit.entities.Key;
import epermit.models.dtos.IssuedPermitDto;
import epermit.models.enums.PermitType;
import epermit.models.inputs.CreatePermitInput;
import epermit.repositories.AuthorityRepository;
import epermit.repositories.IssuedPermitRepository;
import epermit.repositories.KeyRepository;
import epermit.utils.KeyUtil;


@Testcontainers
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class IssuedPermitControllerIT {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    AuthorityRepository authorityRepository;

    @Autowired
    KeyRepository keyRepository;

    @Autowired
    IssuedPermitRepository issuedPermitRepository;

    @Autowired
    KeyUtil keyUtil;

    @BeforeEach
    @Transactional
    void setUp() {
        Authority authority = new Authority();
        authority.setApiUri("apiUri");
        authority.setCode("UZ");
        authority.setName("name");
        authority.setVerifyUri("verifyUri");
        IssuerQuota quota = new IssuerQuota();
        quota.setActive(true);
        quota.setEndNumber(30);
        quota.setStartNumber(1);
        quota.setPermitType(PermitType.BILITERAL);
        quota.setPermitYear(2021);
        authority.addIssuerQuota(quota);
        authorityRepository.save(authority);
        Key key = keyUtil.create("1");
        key.setEnabled(true);
        keyRepository.save(key);
    }

    @Container
    public static PostgreSQLContainer<PermitPostgresContainer> postgreSQLContainer =
            PermitPostgresContainer.getInstance();

    private TestRestTemplate getTestRestTemplate() {
        return testRestTemplate.withBasicAuth("admin", "123456");
    }

    private String getBaseUrl() {
        return "http://localhost:" + port + "/issued_permits";
    }

    @Test
    void getAllTest() {
        for (int i = 0; i < 25; i++) {
            IssuedPermit permit = new IssuedPermit();
            permit.setCompanyName("ABC");
            permit.setIssuedFor("UZ");
            permit.setPermitType(PermitType.BILITERAL);
            permit.setPermitYear(2021);
            permit.setPlateNumber("06AA1234");
            permit.setExpireAt("31/01/2022");
            permit.setIssuedAt("03/03/2021");
            permit.setPermitId("ABC");
            permit.setQrCode("qrCode");
            permit.setSerialNumber(1);
            issuedPermitRepository.save(permit);
        }
        /*
         * final String response = getTestRestTemplate().getForObject(getBaseUrl(), String.class);
         */
        ParameterizedTypeReference<RestResponsePage<IssuedPermitDto>> responseType =
                new ParameterizedTypeReference<RestResponsePage<IssuedPermitDto>>() {};
        ResponseEntity<RestResponsePage<IssuedPermitDto>> result =
                getTestRestTemplate().exchange(getBaseUrl(), HttpMethod.GET, null, responseType);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(25, result.getBody().getTotalElements());

    }

    @Test
    void getByIdTest() {
        IssuedPermit permit = new IssuedPermit();
        permit.setCompanyName("ABC");
        permit.setIssuedFor("UZ");
        permit.setPermitType(PermitType.BILITERAL);
        permit.setPermitYear(2021);
        permit.setPlateNumber("06AA1234");
        permit.setExpireAt("31/01/2022");
        permit.setIssuedAt("03/03/2021");
        permit.setPermitId("ABC");
        permit.setQrCode("qrCode");
        permit.setSerialNumber(1);
        issuedPermitRepository.save(permit);
        IssuedPermitDto dto = getTestRestTemplate()
                .getForObject(getBaseUrl() + "/" + permit.getId(), IssuedPermitDto.class);
        assertEquals("ABC", dto.getPermitId());
    }

    @Test
    void createTest() {
        CreatePermitInput input = new CreatePermitInput();
        input.setCompanyName("ABC");
        input.setIssuedFor("UZ");
        input.setPermitType(PermitType.BILITERAL);
        input.setPermitYear(2021);
        input.setPlateNumber("06AA1234");
        ResponseEntity<String> r =
                getTestRestTemplate().postForEntity(getBaseUrl(), input, String.class);
        assertEquals(HttpStatus.OK, r.getStatusCode());

    }

    @Test
    void revokeTest() {
        IssuedPermit permit = new IssuedPermit();
        permit.setCompanyName("ABC");
        permit.setIssuedFor("UZ");
        permit.setPermitType(PermitType.BILITERAL);
        permit.setPermitYear(2021);
        permit.setPlateNumber("06AA1234");
        permit.setExpireAt("31/01/2022");
        permit.setIssuedAt("03/03/2021");
        permit.setPermitId("ABC");
        permit.setQrCode("qrCode");
        permit.setSerialNumber(1);
        issuedPermitRepository.save(permit);
        HttpEntity<String> entity = new HttpEntity<String>("{}");
        ResponseEntity<Void> r = getTestRestTemplate().exchange(getBaseUrl() + "/" + permit.getId(),
                HttpMethod.DELETE, entity, Void.class);
        assertEquals(HttpStatus.OK, r.getStatusCode());
    }
}
