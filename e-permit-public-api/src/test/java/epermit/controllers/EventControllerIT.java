package epermit.controllers;

import java.util.Map;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import epermit.PermitPostgresContainer;
import epermit.entities.Authority;
import epermit.models.dtos.PrivateKey;
import epermit.repositories.AuthorityRepository;
import epermit.repositories.PrivateKeyRepository;
import epermit.utils.JwsUtil;
import epermit.utils.PrivateKeyUtil;

@Testcontainers
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class EventControllerIT {
        @LocalServerPort
        private int port;

        @Autowired
        private AuthorityRepository authorityRepository;

        @Autowired
        private PrivateKeyRepository keyRepository;


        @Autowired
        private PrivateKeyUtil keyUtil;

        @Autowired
        private TestRestTemplate restTemplate;

        @Autowired
        JwsUtil jwsUtil;

        @MockBean
        ApplicationEventPublisher applicationEventPublisher;


        @Container
        public static PostgreSQLContainer<PermitPostgresContainer> postgreSQLContainer =
                        PermitPostgresContainer.getInstance();


        @BeforeEach
        @Transactional
        void setUp() {
                /*PrivateKey key = keyUtil.create("1");
                key.setEnabled(true);
                keyRepository.save(key);
                Authority authority = new Authority();
                authority.setApiUri("apiUri");
                authority.setCode("UA");
                authority.setName("name");
                authority.setVerifyUri("verifyUri");
                AuthorityKey authorityKey = new AuthorityKey();
                authorityKey.setKeyId("1");
                authorityKey.setJwk(key.getPublicJwk());
                authority.addKey(authorityKey);
                authorityRepository.save(authority);
                CreatedEvent event = new CreatedEvent();
                event.setEventId("1");
                event.setEventType(EventType.KEY_CREATED);
                event.setIssuedFor("UA");
                event.setJws("jws");
                event.setPreviousEventId("0");
                CreatedEvent event2 = new CreatedEvent();
                event2.setEventId("2");
                event2.setEventType(EventType.KEY_CREATED);
                event2.setIssuedFor("UA");
                event2.setJws("jws");
                event2.setPreviousEventId("1");
                createdEventRepository.save(event);
                createdEventRepository.save(event2);*/
        }

        @Test
        void receiveEventTest() {
                final String baseUrl = "http://localhost:" + port + "/events";
                String jws = jwsUtil.createJws(Map.of("issuer", "UA", "issued_for", "TR"));
                HttpHeaders headers = jwsUtil.getJwsHeader(jws);
                HttpEntity<Map<?, ?>> request = new HttpEntity<>(headers);
                ResponseEntity<Boolean> result =
                                this.restTemplate.postForEntity(baseUrl, request, Boolean.class);
                Assert.assertEquals(200, result.getStatusCodeValue());
                Assert.assertEquals(true, result.getBody());
                // verify(applicationEventPublisher).publishEvent(any());
        }

        @Test
        void getEventsTest() {
                final String baseUrl = "http://localhost:" + port + "/events";
                String jws = jwsUtil.createJws(
                                Map.of("issuer", "UA", "issued_for", "TR", "event_id", "1"));
                HttpHeaders headers = jwsUtil.getJwsHeader(jws);
                HttpEntity<?> entity = new HttpEntity<>(headers);
                ResponseEntity<String[]> result = this.restTemplate.exchange(baseUrl,
                                HttpMethod.GET, entity, String[].class);
                Assert.assertEquals(200, result.getStatusCodeValue());
                Assert.assertEquals(1, result.getBody().length);
        }
}


