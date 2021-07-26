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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import epermit.PermitPostgresContainer;
import epermit.RestResponsePage;
import epermit.entities.Authority;
import epermit.entities.LedgerPermit;
import epermit.entities.LedgerQuota;
import epermit.entities.PrivateKey;
import epermit.models.dtos.PermitDto;
import epermit.models.enums.PermitType;
import epermit.models.inputs.CreatePermitInput;
import epermit.models.results.CreatePermitResult;
import epermit.repositories.AuthorityRepository;
import epermit.repositories.LedgerPermitRepository;
import epermit.repositories.PrivateKeyRepository;
import epermit.utils.PrivateKeyUtil;


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
        LedgerQuota quota = new LedgerQuota();
        quota.setActive(true);
        quota.setEndNumber(30);
        quota.setStartNumber(1);
        quota.setPermitType(PermitType.BILITERAL);
        quota.setPermitYear(2021);
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
        return "http://localhost:" + port + "/issued_permits";
    }

    @Test
    void getAllTest() {
        for (int i = 0; i < 25; i++) {
            LedgerPermit permit = new LedgerPermit();
            permit.setCompanyName("ABC");
            permit.setCompanyId("1");
            permit.setIssuedFor("UZ");
            permit.setPermitType(PermitType.BILITERAL);
            permit.setPermitYear(2021);
            permit.setPlateNumber("06AA1234");
            permit.setExpireAt("31/01/2022");
            permit.setIssuedAt("03/03/2021");
            permit.setPermitId("ABC");
            //permit.setQrCode("qrCode");
            permit.setSerialNumber(1);
            permitRepository.save(permit);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getBaseUrl())
                .queryParam("issued_for", "UZ").queryParam("page", 2);

        HttpEntity<?> entity = new HttpEntity<>(headers);

        ParameterizedTypeReference<RestResponsePage<PermitDto>> responseType =
                new ParameterizedTypeReference<RestResponsePage<PermitDto>>() {};
        ResponseEntity<RestResponsePage<PermitDto>> result = getTestRestTemplate()
                .exchange(builder.toUriString(), HttpMethod.GET, entity, responseType);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(25, result.getBody().getTotalElements());
        assertEquals(5, result.getBody().getContent().size());

    }

    @Test
    void getByIdTest() {
        LedgerPermit permit = new LedgerPermit();
        permit.setCompanyName("ABC");
        permit.setCompanyId("1");
        permit.setIssuedFor("UZ");
        permit.setPermitType(PermitType.BILITERAL);
        permit.setPermitYear(2021);
        permit.setPlateNumber("06AA1234");
        permit.setExpireAt("31/01/2022");
        permit.setIssuedAt("03/03/2021");
        permit.setPermitId("ABC");
        //permit.setQrCode("qrCode");
        permit.setSerialNumber(1);
        permitRepository.save(permit);
        PermitDto dto = getTestRestTemplate()
                .getForObject(getBaseUrl() + "/" + permit.getId(), PermitDto.class);
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
        ResponseEntity<CreatePermitResult> r =
                getTestRestTemplate().postForEntity(getBaseUrl(), input, CreatePermitResult.class);
        assertEquals(HttpStatus.OK, r.getStatusCode());

    }

    @Test
    void revokeTest() {
        LedgerPermit permit = new LedgerPermit();
        permit.setCompanyName("ABC");
        permit.setIssuedFor("UZ");
        permit.setPermitType(PermitType.BILITERAL);
        permit.setPermitYear(2021);
        permit.setPlateNumber("06AA1234");
        permit.setExpireAt("31/01/2022");
        permit.setIssuedAt("03/03/2021");
        permit.setPermitId("ABC");
        //permit.setQrCode("qrCode");
        permit.setSerialNumber(1);
        permitRepository.save(permit);
        HttpEntity<String> entity = new HttpEntity<String>("{}");
        ResponseEntity<Void> r = getTestRestTemplate().exchange(getBaseUrl() + "/" + permit.getId(),
                HttpMethod.DELETE, entity, Void.class);
        assertEquals(HttpStatus.OK, r.getStatusCode());
    }
}


/*
 * final String response = getTestRestTemplate().getForObject(getBaseUrl(), String.class);
 */
