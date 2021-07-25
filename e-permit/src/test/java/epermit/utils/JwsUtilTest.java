package epermit.utils;

import static org.mockito.Mockito.when;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.ECDSAVerifier;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.gen.ECKeyGenerator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import epermit.entities.LedgerPublicKey;
import epermit.models.EPermitProperties;
import epermit.models.results.JwsValidationResult;
import epermit.repositories.LedgerPublicKeyRepository;
import lombok.SneakyThrows;

@ExtendWith(MockitoExtension.class)
public class JwsUtilTest {
    @Mock
    EPermitProperties properties;

    @Mock
    PrivateKeyUtil keyUtil;

    @Mock
    LedgerPublicKeyRepository publicKeyRepository;

    @InjectMocks
    JwsUtil util;

    @Test
    @SneakyThrows
    void createJwsTest() {
        ECKey key = new ECKeyGenerator(Curve.P_256).keyUse(KeyUse.SIGNATURE).keyID("1").generate();
        when(keyUtil.getKey()).thenReturn(key);
        Map<String, String> claims = new HashMap<>();
        claims.put("issuer", "TR");
        String jws = util.createJws(claims);
        JWSObject jwsObject = JWSObject.parse(jws);
        Map<String, Object> payload = jwsObject.getPayload().toJSONObject();
        Assertions.assertEquals(payload.get("issuer"), "TR");
        JWSVerifier verifier = new ECDSAVerifier(key);
        Boolean valid = jwsObject.verify(verifier);
        Assertions.assertTrue(valid);
    }

    @Test
    @SneakyThrows
    void validateJwsOkTest() {
        ECKey key = new ECKeyGenerator(Curve.P_256).keyUse(KeyUse.SIGNATURE).keyID("1").generate();
        Map<String, String> claims = new HashMap<>();
        claims.put("issuer", "TR");
        claims.put("issued_for", "UA");
        String jws = util.createJws(key, claims);
        LedgerPublicKey authorityKey = new LedgerPublicKey();
        authorityKey.setJwk(key.toPublicJWK().toJSONString());
        when(publicKeyRepository.findOneByAuthorityCodeAndKeyId("TR", "1"))
                .thenReturn(Optional.of(authorityKey));
        when(properties.getIssuerCode()).thenReturn("UA");
        Boolean r = util.validateJws(jws);
        Assertions.assertTrue(r);
    }

    @Test
    @SneakyThrows
    void validateJwsInvalidIssuedForTest() {
        ECKey key = new ECKeyGenerator(Curve.P_256).keyUse(KeyUse.SIGNATURE).keyID("1").generate();
        Map<String, String> claims = new HashMap<>();
        claims.put("issuer", "TR");
        claims.put("issued_for", "UA2");
        String jws = util.createJws(key, claims);
        when(properties.getIssuerCode()).thenReturn("UA");
        Boolean r = util.validateJws(jws);
        Assertions.assertFalse(r);
    }

    @Test
    @SneakyThrows
    void validateJwsInvalidKeyIdTest() {
        ECKey key = new ECKeyGenerator(Curve.P_256).keyUse(KeyUse.SIGNATURE).keyID("1").generate();
        Map<String, String> claims = new HashMap<>();
        claims.put("issuer", "TR");
        claims.put("issued_for", "UA");
        String jws = util.createJws(key, claims);
        when(properties.getIssuerCode()).thenReturn("UA");
        Boolean r = util.validateJws(jws);
        Assertions.assertFalse(r);
    }

}











    /*@Test
    void timeTest(){
        Long t =  OffsetDateTime.now(ZoneOffset.UTC).toEpochSecond();
        Long l = Instant.now().toEpochMilli();
        Timestamp timestamp = new Timestamp(l);
        System.out.println(t);
        System.out.println(timestamp.toInstant());
        System.out.println(l);
        System.out.println(Instant.now().getEpochSecond());
        System.out.println(LocalDateTime.now());
        System.out.println(OffsetDateTime.now());
        System.out.println(LocalDateTime.now(ZoneOffset.UTC));
        System.out.println(LocalDateTime.now(ZoneOffset.UTC).toInstant(ZoneOffset.UTC).toEpochMilli());
    }

    @Test
    void dateTest() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate  date = LocalDate.parse("03/02/2021", dtf);
    }*/