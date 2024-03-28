package epermit.controllers;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
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
import epermit.models.dtos.PermitDto;
import epermit.models.dtos.PermitListItem;
import epermit.models.enums.PermitActivityType;
import epermit.models.enums.PermitType;
import epermit.models.inputs.CreatePermitInput;
import epermit.models.inputs.PermitUsedInput;
import epermit.models.results.CreatePermitResult;
import epermit.repositories.AuthorityRepository;
import epermit.repositories.LedgerPermitRepository;
import epermit.repositories.LedgerQuotaRepository;


@Testcontainers
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
// @TestPropertySource(properties = {"EPERMIT_VERIFIER_PASSWORD = 123"})
public class PermitControllerIT {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    AuthorityRepository authorityRepository;

    @Autowired
    LedgerPermitRepository permitRepository;

    @Autowired
    LedgerQuotaRepository ledgerQuotaRepository;

    @MockBean
    AppEventListener appEventListener;

    @MockBean
    RestTemplate restTemplate;

    @BeforeEach
    @Transactional
    void setUp() {
        Authority authority = new Authority();
        authority.setPublicApiUri("http://api.gov");
        authority.setCode("UZ");
        authority.setName("Uzbekistan");
        authorityRepository.save(authority);
        LedgerQuota quota = new LedgerQuota();
        quota.setBalance(30L);
        quota.setPermitType(PermitType.BILATERAL);
        quota.setPermitYear(2021);
        quota.setPermitIssuer("TR");
        quota.setPermitIssuedFor("UZ");
        ledgerQuotaRepository.save(quota);
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
            permit.setSerialNumber(1L);
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
        permit.setSerialNumber(1L);
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
        permit.setSerialNumber(1L);
        permitRepository.save(permit);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), any()))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));
        HttpEntity<String> entity = new HttpEntity<String>("{}");
        ResponseEntity<Void> r = getTestRestTemplate().exchange(
                getBaseUrl() + "/" + permit.getPermitId(), HttpMethod.DELETE, entity, Void.class);
        assertEquals(HttpStatus.OK, r.getStatusCode());
    }

    @Test
    void usePermitTest() {
        Authority authority = new Authority();
        authority.setPublicApiUri("apiUri");
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
        permit.setSerialNumber(1L);
        permitRepository.save(permit);
        PermitUsedInput input = new PermitUsedInput();
        input.setActivityType(PermitActivityType.ENTRANCE);
        input.setActivityTimestamp(0L);
        ResponseEntity<?> r = getTestRestTemplate().postForEntity(
                getBaseUrl() + "/" + permit.getPermitId() + "/activities", input, String.class);
        assertEquals(HttpStatus.OK, r.getStatusCode());
    }
}
