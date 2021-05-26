package epermit.services;

import static org.junit.jupiter.api.Assertions.assertTrue;
import java.time.Instant;
import java.util.UUID;
import com.nimbusds.jose.jwk.ECKey;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import epermit.PermitPostgresContainer;
import epermit.entities.Authority;
import epermit.entities.AuthorityKey;
import epermit.entities.Key;
import epermit.events.EventType;
import epermit.events.EventValidationResult;
import epermit.events.quotacreated.QuotaCreatedEvent;
import epermit.models.enums.PermitType;
import epermit.repositories.AuthorityRepository;
import epermit.utils.JwsUtil;
import epermit.utils.KeyUtil;
import lombok.SneakyThrows;

@Testcontainers
@SpringBootTest(properties = {"spring.datasource.url=${SPRING_DATASOURCE_URL}",
        "spring.datasource.username=${SPRING_DATASOURCE_USERNAME}",
        "spring.datasource.password=${SPRING_DATASOURCE_PASSWORD}",
        "spring.datasource.driver-class-name=org.postgresql.Driver",
        "spring.jpa.hibernate.ddl-auto=create-drop", "epermit.issuer-code=TR",
        "epermit.key-password=123456"})
@ActiveProfiles("test")
public class ReceivedEventServiceIT {

    @Autowired
    private KeyUtil keyUtil;

    @Autowired
    private JwsUtil jwsUtil;

    @Autowired
    private AuthorityRepository authorityRepository;


    @Autowired
    private ReceivedEventService receivedEventService;

    @Container
    public static PostgreSQLContainer<PermitPostgresContainer> postgreSQLContainer =
            PermitPostgresContainer.getInstance();


    @Transactional
    Key setUp() {
        Key key = keyUtil.create("1");
        Authority authority = new Authority();
        authority.setCode("UZ");
        authority.setApiUri("apiUri");
        authority.setName("Uzbekistan");
        authority.setVerifyUri("verifyUri");
        AuthorityKey authorityKey = new AuthorityKey();
        authorityKey.setActive(true);
        authorityKey.setAuthority(authority);
        authorityKey.setJwk(key.getPublicJwk());
        authorityKey.setKeyId("1");
        authorityKey.setValidFrom(Instant.now().getEpochSecond());
        authority.addKey(authorityKey);
        authorityRepository.save(authority);
        return key;
    }

    @Test
    @SneakyThrows
    void handleTest() {
        Key key = setUp();
        QuotaCreatedEvent e = new QuotaCreatedEvent();
        e.setEndNumber(10);
        e.setPermitType(PermitType.BILITERAL);
        e.setPermitYear(2021);
        e.setStartNumber(1);
        e.setEventType(EventType.QUOTA_CREATED);
        e.setPreviousEventId("0");
        e.setCreatedAt(Instant.now().getEpochSecond());
        e.setIssuer("UZ");
        e.setIssuedFor("TR");
        e.setEventId(UUID.randomUUID().toString());
        String jws = jwsUtil.createJws(getKey(key), e);
        EventValidationResult r = receivedEventService.handle(jws);
        assertTrue(r.isOk());
    }

    @SneakyThrows
    public ECKey getKey(Key key) {
        TextEncryptor decryptor = Encryptors.text("123456", key.getSalt());
        ECKey ecKey = ECKey.parse(decryptor.decrypt(key.getPrivateJwk()));
        return ecKey;
    }
}
