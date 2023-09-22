package epermit.controllers;

import java.time.Instant;
import java.util.Map;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import epermit.PermitPostgresContainer;
import epermit.commons.GsonUtil;
import epermit.entities.Authority;
import epermit.entities.LedgerPublicKey;
import epermit.entities.LedgerQuota;
import epermit.ledgerevents.LedgerEventResult;
import epermit.ledgerevents.keycreated.KeyCreatedLedgerEvent;
import epermit.ledgerevents.keyrevoked.KeyRevokedLedgerEvent;
import epermit.ledgerevents.permitcreated.PermitCreatedLedgerEvent;
import epermit.ledgerevents.quotacreated.QuotaCreatedLedgerEvent;
import epermit.models.dtos.PrivateKey;
import epermit.models.dtos.PublicJwk;
import epermit.models.enums.PermitType;
import epermit.repositories.AuthorityRepository;
import epermit.repositories.LedgerEventRepository;
import epermit.repositories.LedgerPublicKeyRepository;
import epermit.repositories.LedgerQuotaRepository;
import epermit.utils.JwsUtil;
import epermit.utils.PrivateKeyUtil;

@Testcontainers
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class EventControllerIT {
        @LocalServerPort
        private int port;

        @Autowired
        private LedgerPublicKeyRepository ledgerPublicKeyRepository;

        @Autowired
        private LedgerQuotaRepository ledgerQuotaRepository;

        @Autowired
        private PrivateKeyUtil keyUtil;

        @Autowired
        private TestRestTemplate restTemplate;

        @Autowired
        JwsUtil jwsUtil;

        @Autowired
        LedgerEventRepository ledgerEventRepository;

        static String previousEventId = "0";

        @Container
        public static PostgreSQLContainer<PermitPostgresContainer> postgreSQLContainer =
                        PermitPostgresContainer.getInstance();


        @BeforeAll
        @Transactional
        public static void setUp(@Autowired AuthorityRepository authorityRepository,
                        @Autowired PrivateKeyUtil keyUtil,
                        @Autowired LedgerPublicKeyRepository ledgerPublicKeyRepository) {
                Authority authority = new Authority();
                authority.setApiUri("apiUri");
                authority.setCode("UZ");
                authority.setName("name");
                authorityRepository.save(authority);
                LedgerPublicKey ledgerKey = new LedgerPublicKey();
                ledgerKey.setAuthorityCode("UZ");
                ledgerKey.setKeyId("1");
                ledgerKey.setJwk(keyUtil.getKey().toJSONString());
                ledgerPublicKeyRepository.save(ledgerKey);
        }

        @Test
        void notFoundTest() {
                final String baseUrl = "http://localhost:" + port + "/events/quota-created2";

                HttpEntity<String> request = new HttpEntity<>("");

                ResponseEntity<?> result =
                                this.restTemplate.postForEntity(baseUrl, request, Void.class);
                Assert.assertEquals(404, result.getStatusCode());
        }

        @Test
        void quotaCreatedEventInvalidTest() {
                final String baseUrl = "http://localhost:" + port + "/events/quota-created";
                QuotaCreatedLedgerEvent event = new QuotaCreatedLedgerEvent("UZ", "TR", "0");
                event.setEndNumber(1);
                event.setPermitIssuedFor("UZ");
                event.setPermitIssuer("TR");
                event.setPermitType(PermitType.BILITERAL);
                event.setPermitYear(2021);
                event.setStartNumber(100);
                String jws = jwsUtil.createJws(GsonUtil.toMap(event));
                HttpHeaders headers = createEventRequestHeader(jws);
                HttpEntity<Map<?, ?>> request = new HttpEntity<>(GsonUtil.toMap(event), headers);

                ResponseEntity<?> result =
                                this.restTemplate.postForEntity(baseUrl, request, Object.class);
                Assert.assertEquals(400, result.getStatusCode());
        }

        @Test
        void quotaCreatedEventOkTest() {
                final String baseUrl = "http://localhost:" + port + "/events/quota-created";
                QuotaCreatedLedgerEvent event =
                                new QuotaCreatedLedgerEvent("UZ", "TR", previousEventId);
                previousEventId = event.getEventId();
                event.setEndNumber(100);
                event.setPermitIssuedFor("UZ");
                event.setPermitIssuer("TR");
                event.setPermitType(PermitType.BILITERAL);
                event.setPermitYear(2021);
                event.setStartNumber(1);
                String jws = jwsUtil.createJws(GsonUtil.toMap(event));
                HttpHeaders headers = createEventRequestHeader(jws);
                HttpEntity<Map<?, ?>> request = new HttpEntity<>(GsonUtil.toMap(event), headers);

                ResponseEntity<?> result =
                                this.restTemplate.postForEntity(baseUrl, request, Void.class);
                Assert.assertEquals(200, result.getStatusCode());

        }



        @Test
        void quotaCreatedEventTest() {
                final String baseUrl = "http://localhost:" + port + "/events/quota-created";
                // Boolean isPresent =
                // ledgerEventRepository.findOneByProducerAndConsumerAndEventId("UZ", "TR",
                // previousEventId).isPresent();
                QuotaCreatedLedgerEvent event =
                                new QuotaCreatedLedgerEvent("UZ", "TR", previousEventId);
                previousEventId = event.getEventId();
                event.setEndNumber(200);
                event.setPermitIssuedFor("UZ");
                event.setPermitIssuer("TR");
                event.setPermitType(PermitType.BILITERAL);
                event.setPermitYear(2021);
                event.setStartNumber(101);
                String jws = jwsUtil.createJws(GsonUtil.toMap(event));
                HttpHeaders headers = createEventRequestHeader(jws);
                HttpEntity<Map<?, ?>> request = new HttpEntity<>(GsonUtil.toMap(event), headers);

                ResponseEntity<LedgerEventResult> result = this.restTemplate.postForEntity(baseUrl,
                                request, LedgerEventResult.class);
                Assert.assertEquals(200, result.getStatusCode());
                QuotaCreatedLedgerEvent event2 =
                                new QuotaCreatedLedgerEvent("UZ", "TR", previousEventId);
                event2.setEndNumber(100);
                event2.setPermitIssuedFor("UZ");
                event2.setPermitIssuer("TR");
                event2.setPermitType(PermitType.BILITERAL);
                event2.setPermitYear(2021);
                event2.setStartNumber(1);
                String jws2 = jwsUtil.createJws(GsonUtil.toMap(event2));
                HttpHeaders headers2 = createEventRequestHeader(jws2);
                HttpEntity<Map<?, ?>> request2 = new HttpEntity<>(GsonUtil.toMap(event2), headers2);
                ResponseEntity<?> result2 =
                                this.restTemplate.postForEntity(baseUrl, request2, Object.class);
                Assert.assertEquals(422, result2.getStatusCode());

        }

        @Test
        void keyCreatedEventOkTest() {
                final String baseUrl = "http://localhost:" + port + "/events/key-created";
                KeyCreatedLedgerEvent event =
                                new KeyCreatedLedgerEvent("UZ", "TR", previousEventId);
                previousEventId = event.getEventId();
                PrivateKey key = keyUtil.create("2");
                event.setKid(key.getKeyId());
                PublicJwk jwk = GsonUtil.getGson().fromJson(key.getPublicJwk(), PublicJwk.class);
                event.setAlg(jwk.getAlg());
                event.setCrv(jwk.getCrv());
                event.setKty(jwk.getKty());
                event.setUse(jwk.getUse());
                event.setX(jwk.getX());
                event.setY(jwk.getY());
                String jws = jwsUtil.createJws(GsonUtil.toMap(event));
                HttpHeaders headers = createEventRequestHeader(jws);
                HttpEntity<Map<?, ?>> request = new HttpEntity<>(GsonUtil.toMap(event), headers);

                ResponseEntity<?> result =
                                this.restTemplate.postForEntity(baseUrl, request, String.class);
                Assert.assertEquals(200, result.getStatusCode());
        }


        @Test
        void keyRevokedEventOkTest() {
                PrivateKey key = keyUtil.create("3");
                LedgerPublicKey ledgerKey = new LedgerPublicKey();
                ledgerKey.setAuthorityCode("UZ");
                ledgerKey.setKeyId("3");
                ledgerKey.setJwk(key.getPublicJwk());
                ledgerPublicKeyRepository.save(ledgerKey);
                final String baseUrl = "http://localhost:" + port + "/events/key-revoked";
                KeyRevokedLedgerEvent event =
                                new KeyRevokedLedgerEvent("UZ", "TR", previousEventId);
                previousEventId = event.getEventId();
                event.setKeyId("3");
                event.setRevokedAt(Instant.now().getEpochSecond());
                String jws = jwsUtil.createJws(GsonUtil.toMap(event));
                HttpHeaders headers = createEventRequestHeader(jws);
                HttpEntity<Map<?, ?>> request = new HttpEntity<>(GsonUtil.toMap(event), headers);

                ResponseEntity<?> result =
                                this.restTemplate.postForEntity(baseUrl, request, String.class);
                Assert.assertEquals(200, result.getStatusCode());
        }

        @Test
        void permitCreatedEventOkTest() {
                LedgerQuota quota = new LedgerQuota();
                quota.setActive(true);
                quota.setEndNumber(100);
                quota.setPermitIssuedFor("TR");
                quota.setPermitIssuer("UZ");
                quota.setPermitType(PermitType.BILITERAL);
                quota.setPermitYear(2021);
                quota.setStartNumber(1);
                ledgerQuotaRepository.save(quota);
                final String baseUrl = "http://localhost:" + port + "/events/permit-created";
                PermitCreatedLedgerEvent event =
                                new PermitCreatedLedgerEvent("UZ", "TR", previousEventId);
                previousEventId = event.getEventId();
                event.setCompanyId("ABC");
                event.setCompanyName("ABC");
                event.setExpireAt("31/01/2022");
                event.setIssuedAt("01/09/2021");
                event.setPermitId("UZ-TR-2021-1-1");
                event.setPermitIssuedFor("TR");
                event.setPermitIssuer("UZ");
                event.setPermitType(PermitType.BILITERAL);
                event.setPermitYear(2021);
                event.setPlateNumber("ABC");
                event.setSerialNumber(1);
                event.setQrCode("1");
                String jws = jwsUtil.createJws(GsonUtil.toMap(event));
                HttpHeaders headers = createEventRequestHeader(jws);
                HttpEntity<Map<?, ?>> request = new HttpEntity<>(GsonUtil.toMap(event), headers);

                ResponseEntity<?> result =
                                this.restTemplate.postForEntity(baseUrl, request, String.class);
                Assert.assertEquals(200, result.getStatusCode());
        }


        private HttpHeaders createEventRequestHeader(String jws) {
                String[] proofArr = jws.split("\\.");
                String proof = proofArr[0] + "." + proofArr[2];
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.add("Authorization", "Bearer " + proof);
                return headers;
        }
}


