package epermit.integrations;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.TestPropertySourceUtils;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import epermit.Application;
import epermit.common.CustomPostgresContainer;
import epermit.common.EventType;
import epermit.entities.Authority;
import epermit.entities.AuthorityKey;
import epermit.entities.Key;
import epermit.entities.ReceivedEvent;
import epermit.events.EventHandleResult;
import epermit.events.AppEvent;
import epermit.services.KeyService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Testcontainers
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Slf4j
public class EventControllerIT {

        @LocalServerPort
        private int port;

        @Autowired
        private TestRestTemplate restTemplate;

        @Autowired
        private KeyService keyService;

        @Container
        public static PostgreSQLContainer<CustomPostgresContainer> postgreSQLContainer = CustomPostgresContainer
                        .getInstance();

        @Test
        @SneakyThrows
        public void createTest() {
                Key k = keyService.create("1");
                Authority authority = new Authority();
                AuthorityKey authorityKey = new AuthorityKey();
                authorityKey.setJwk(keyService.getKey().toPublicJWK().toJSONString());
                authorityKey.setKid("1");
                authority.addKey(authorityKey);
                ReceivedEvent event = new ReceivedEvent();
                event.setEventId("0");
                event.setPreviousEventId("0");
                event.setEventType(EventType.KEY_CREATED);
                Map<String, String> claims = new HashMap<>();
                claims.put("event_type", "KEY_CREATED");
                claims.put("event_id", "1");
                claims.put("previous_event_id", "0");
                claims.put("issuer", "UA");
                claims.put("issued_for", "TR");
                String jws = keyService.createJws(claims);
                final String baseUrl = "http://localhost:" + port + "/events/";
                URI uri = new URI(baseUrl);
                AppEvent input = new AppEvent();
                input.setJws(jws);

                HttpHeaders headers = new HttpHeaders();
                HttpEntity<AppEvent> request = new HttpEntity<>(input, headers);
                ResponseEntity<EventHandleResult> result = this.restTemplate.postForEntity(uri, request,
                                EventHandleResult.class);
                Assert.assertEquals(200, result.getStatusCodeValue());
                Assert.assertEquals(true, result.getBody().isSucceed());

        }

        void test() {
                SpringApplicationBuilder uws = new SpringApplicationBuilder(Application.class).properties(
                                "server.port=8081", "spring.jpa.hibernate.ddl-auto=create-drop",
                                "spring.datasource.driver-class-name=org.postgresql.Driver",
                                "spring.datasource.url=" + postgreSQLContainer.getJdbcUrl(),
                                "spring.datasource.username=" + postgreSQLContainer.getUsername(),
                                "spring.datasource.password=" + postgreSQLContainer.getPassword());
                uws.run();
        }
        /*
         * static class Initializer implements
         * ApplicationContextInitializer<ConfigurableApplicationContext> {
         * 
         * @Override public void initialize(ConfigurableApplicationContext
         * applicationContext) {
         * TestPropertySourceUtils.addInlinedPropertiesToEnvironment(applicationContext,
         * "spring.datasource.url=" + postgreSQLContainer.getJdbcUrl());
         * TestPropertySourceUtils.addInlinedPropertiesToEnvironment(applicationContext,
         * "spring.datasource.username=" + postgreSQLContainer.getUsername());
         * TestPropertySourceUtils.addInlinedPropertiesToEnvironment(applicationContext,
         * "spring.datasource.password=" + postgreSQLContainer.getPassword());
         * TestPropertySourceUtils.addInlinedPropertiesToEnvironment(applicationContext,
         * "spring.datasource.driver-class-name=org.postgresql.Driver");
         * TestPropertySourceUtils.addInlinedPropertiesToEnvironment(applicationContext,
         * "spring.jpa.hibernate.ddl-auto=create-drop"); } }
         */
}
