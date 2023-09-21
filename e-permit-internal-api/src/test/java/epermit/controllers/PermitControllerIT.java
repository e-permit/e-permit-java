package epermit.controllers;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import epermit.AppEventListener;
import epermit.PermitPostgresContainer;
import epermit.RestResponsePage;
import epermit.commons.GsonUtil;
import epermit.entities.Authority;
import epermit.entities.LedgerPermit;
import epermit.entities.LedgerQuota;
import epermit.entities.PrivateKey;
import epermit.entities.SerialNumber;
import epermit.models.dtos.PermitDto;
import epermit.models.dtos.PermitListItem;
import epermit.models.enums.PermitActivityType;
import epermit.models.enums.PermitType;
import epermit.models.enums.SerialNumberState;
import epermit.models.inputs.CreatePermitInput;
import epermit.models.inputs.PermitUsedInput;
import epermit.models.results.CreatePermitResult;
import epermit.repositories.AuthorityRepository;
import epermit.repositories.LedgerPermitRepository;
import epermit.repositories.LedgerQuotaRepository;
import epermit.repositories.PrivateKeyRepository;
import epermit.repositories.SerialNumberRepository;
import epermit.utils.PrivateKeyUtil;


@Testcontainers
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
// @TestPropertySource(properties = {"EPERMIT_VERIFIER_PASSWORD = 123"})
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
    LedgerQuotaRepository ledgerQuotaRepository;

    @Autowired
    PrivateKeyUtil keyUtil;

    @Autowired
    SerialNumberRepository serialNumberRepository;

    @MockBean
    AppEventListener appEventListener;

    @MockBean
    RestTemplate restTemplate;

    @BeforeEach
    @Transactional
    void setUp() {
        Authority authority = new Authority();
        authority.setApiUri("http://api.gov");
        authority.setCode("UZ");
        authority.setName("Uzbekistan");
        authorityRepository.save(authority);
        LedgerQuota quota = new LedgerQuota();
        quota.setActive(true);
        quota.setEndNumber(30);
        quota.setStartNumber(1);
        quota.setPermitType(PermitType.BILATERAL);
        quota.setPermitYear(2021);
        quota.setPermitIssuer("TR");
        quota.setPermitIssuedFor("UZ");
        ledgerQuotaRepository.save(quota);
        List<SerialNumber> serialNumbers = new ArrayList<>();
        for (int i = 1; i <= 30; i++) {
            SerialNumber serialNumber = new SerialNumber();
            serialNumber.setSerialNumber(i);
            serialNumber.setAuthorityCode("UZ");
            serialNumber.setPermitType(PermitType.BILATERAL);
            serialNumber.setPermitYear(2021);
            serialNumber.setState(SerialNumberState.CREATED);
            serialNumbers.add(serialNumber);
        }
        serialNumberRepository.saveAll(serialNumbers);
        epermit.models.dtos.PrivateKey key = keyUtil.create("1");
        PrivateKey keyEntity = new PrivateKey();
        keyEntity.setKeyId(key.getKeyId());
        keyEntity.setPrivateJwk(key.getPrivateJwk());
        keyEntity.setSalt(key.getSalt());
        keyEntity.setEnabled(true);
        keyRepository.save(keyEntity);
    }

    @Container
    public static PostgreSQLContainer<PermitPostgresContainer> postgreSQLContainer =
            PermitPostgresContainer.getInstance();

    private TestRestTemplate getTestRestTemplate() {
        return testRestTemplate.withBasicAuth("admin", "123456");
    }

    private TestRestTemplate getTestRestTemplateForVerifier() {
        return testRestTemplate.withBasicAuth("verifier", "123");
    }

    private String getBaseUrl() {
        return "http://localhost:" + port + "/permits";
    }

    @Test
    void getAllTest() {
        for (int i = 0; i < 25; i++) {
            LedgerPermit permit = new LedgerPermit();
            permit.setIssuer("TR");
            permit.setCompanyName("ABC");
            permit.setCompanyId("1");
            permit.setIssuedFor("UZ");
            permit.setPermitType(PermitType.BILATERAL);
            permit.setPermitYear(2021);
            permit.setPlateNumber("06AA1234");
            permit.setExpireAt("31/01/2022");
            permit.setIssuedAt("03/03/2021");
            permit.setPermitId("ABC");
            permit.setQrCode("qrCode");
            permit.setSerialNumber(1);
            permitRepository.save(permit);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(getBaseUrl())
                .queryParam("issued_for", "UZ").queryParam("page", 2);

        HttpEntity<?> entity = new HttpEntity<>(headers);

        ParameterizedTypeReference<RestResponsePage<PermitListItem>> responseType =
                new ParameterizedTypeReference<RestResponsePage<PermitListItem>>() {};
        ResponseEntity<RestResponsePage<PermitListItem>> result = getTestRestTemplate()
                .exchange(builder.toUriString(), HttpMethod.GET, entity, responseType);
        assertEquals(HttpStatus.OK, result.getStatusCode());
         var body = result.getBody();
        Assert.assertNotNull("Null body", body);
        assertEquals(25, body.getTotalElements());
        assertEquals(5, body.getContent().size());

    }

    @Test
    void getByIdTest() {
        LedgerPermit permit = new LedgerPermit();
        permit.setCompanyName("ABC");
        permit.setCompanyId("1");
        permit.setIssuer("TR");
        permit.setIssuedFor("UZ");
        permit.setPermitType(PermitType.BILATERAL);
        permit.setPermitYear(2021);
        permit.setPlateNumber("06AA1234");
        permit.setExpireAt("31/01/2022");
        permit.setIssuedAt("03/03/2021");
        permit.setPermitId("ABC");
        permit.setQrCode("qrCode");
        permit.setSerialNumber(1);
        permitRepository.save(permit);
        PermitDto dto = getTestRestTemplate().getForObject(getBaseUrl() + "/" + permit.getId(),
                PermitDto.class);
        assertEquals("ABC", dto.getPermitId());
    }

    @Test
    void createTest() {
        CreatePermitInput input = new CreatePermitInput();
        input.setCompanyName("ABC");
        input.setCompanyId("123");
        input.setIssuedFor("UZ");
        input.setPermitType(PermitType.BILATERAL);
        input.setPermitYear(2021);
        input.setPlateNumber("06AA1234");
        ResponseEntity<String> r =
                getTestRestTemplate().postForEntity(getBaseUrl(), input, String.class);
        GsonUtil.getGson().fromJson(r.getBody(), CreatePermitResult.class);
        assertEquals(HttpStatus.OK, r.getStatusCode());
    }

    @Test
    void revokeTest() {
        LedgerPermit permit = new LedgerPermit();
        permit.setCompanyId("123");
        permit.setCompanyName("ABC");
        permit.setIssuer("TR");
        permit.setIssuedFor("UZ");
        permit.setPermitType(PermitType.BILATERAL);
        permit.setPermitYear(2021);
        permit.setPlateNumber("06AA1234");
        permit.setExpireAt("31/01/2022");
        permit.setIssuedAt("03/03/2021");
        permit.setPermitId("ABC");
        permit.setQrCode("qrCode");
        permit.setSerialNumber(1);
        permitRepository.save(permit);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), any()))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));
        HttpEntity<String> entity = new HttpEntity<String>("{}");
        ResponseEntity<Void> r = getTestRestTemplate().exchange(
                getBaseUrl() + "/" + permit.getPermitId(), HttpMethod.DELETE, entity, Void.class);
        assertEquals(HttpStatus.OK, r.getStatusCode());
    }

    @Test
    void revokeUnauthorizedTest() {
        HttpEntity<String> entity = new HttpEntity<String>("{}");
        ResponseEntity<?> r = getTestRestTemplateForVerifier().exchange(getBaseUrl() + "/" + "12",
                HttpMethod.DELETE, entity, String.class);
        System.out.println(r.getBody());
        assertEquals(HttpStatus.FORBIDDEN, r.getStatusCode());
    }

    @Test
    void usePermitTest() {
        Authority authority = new Authority();
        authority.setApiUri("apiUri");
        authority.setCode("TR");
        authority.setName("Uzbekistan");
        authorityRepository.save(authority);
        LedgerPermit permit = new LedgerPermit();
        permit.setCompanyId("123");
        permit.setCompanyName("ABC");
        permit.setIssuer("UZ");
        permit.setIssuedFor("TR");
        permit.setPermitType(PermitType.BILATERAL);
        permit.setPermitYear(2021);
        permit.setPlateNumber("06AA1234");
        permit.setExpireAt("31/01/2022");
        permit.setIssuedAt("03/03/2021");
        permit.setPermitId("ABC");
        permit.setQrCode("qrCode");
        permit.setSerialNumber(1);
        permitRepository.save(permit);
        PermitUsedInput input = new PermitUsedInput();
        input.setActivityType(PermitActivityType.ENTRANCE);
        input.setActivityTimestamp(0L);
        ResponseEntity<?> r = getTestRestTemplateForVerifier().postForEntity(
                getBaseUrl() + "/" + permit.getPermitId() + "/activities", input, String.class);
        assertEquals(HttpStatus.OK, r.getStatusCode());
    }
}
