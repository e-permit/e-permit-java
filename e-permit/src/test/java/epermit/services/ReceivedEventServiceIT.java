package epermit.services;

import static org.junit.jupiter.api.Assertions.assertTrue;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import com.nimbusds.jose.jwk.ECKey;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
import epermit.entities.Permit;
import epermit.entities.VerifierQuota;
import epermit.events.EventType;
import epermit.events.EventValidationResult;
import epermit.events.permitcreated.PermitCreatedEvent;
import epermit.events.quotacreated.QuotaCreatedEvent;
import epermit.models.enums.PermitType;
import epermit.repositories.AuthorityRepository;
import epermit.repositories.PermitRepository;
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

    @Autowired
    private PermitRepository permitRepository;

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
        authority.addKey(authorityKey);
        VerifierQuota quota = new VerifierQuota();
        quota.setActive(true);
        quota.setEndNumber(5);
        quota.setStartNumber(1);
        quota.setPermitType(PermitType.BILITERAL);
        quota.setPermitYear(2021);
        authority.addVerifierQuota(quota);
        authorityRepository.save(authority);
        return key;
    }

    @Test
    @SneakyThrows
    void handleQuotaCreatedEventTest() {
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

    @Test
    @SneakyThrows
    void handlePermitCreatedEventTest() {
        Key key = setUp();
        PermitCreatedEvent e = new PermitCreatedEvent();
        e.setCompanyName("ABC");
        e.setPermitType(PermitType.BILITERAL);
        e.setPermitYear(2021);
        e.setExpireAt("31/01/2022");
        e.setIssuedAt("03/01/2021");
        e.setPlateNumber("06BBBBBB");
        e.setPermitId("UZ-TR-2021-1-5");
        e.setSerialNumber(5);
        e.setEventType(EventType.PERMIT_CREATED);
        e.setPreviousEventId("0");
        e.setCreatedAt(Instant.now().getEpochSecond());
        e.setIssuer("UZ");
        e.setIssuedFor("TR");
        e.setEventId(UUID.randomUUID().toString());
        String jws = jwsUtil.createJws(getKey(key), e);
        EventValidationResult r = receivedEventService.handle(jws);
        assertTrue(r.isOk());
        Optional<Permit> p = permitRepository.findOneByIssuerAndPermitId("UZ", "UZ-TR-2021-1-5");
        assertTrue(p.isPresent());
    }


    @SneakyThrows
    public ECKey getKey(Key key) {
        TextEncryptor decryptor = Encryptors.text("123456", key.getSalt());
        ECKey ecKey = ECKey.parse(decryptor.decrypt(key.getPrivateJwk()));
        return ecKey;
    }
}
