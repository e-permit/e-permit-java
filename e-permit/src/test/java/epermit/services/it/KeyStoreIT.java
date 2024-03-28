package epermit.services.it;

import java.io.File;
import java.io.FileInputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.ResourceUtils;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.ECDSAVerifier;
import com.nimbusds.jose.crypto.bc.BouncyCastleProviderSingleton;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.gen.ECKeyGenerator;

import epermit.PermitPostgresContainer;
import epermit.commons.GsonUtil;
import epermit.models.dtos.PublicJwk;
import epermit.services.JwsService;
import epermit.services.EPermitKeyStore;
import lombok.SneakyThrows;

@Testcontainers
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class KeyStoreIT {

    @Autowired
    private JwsService jwsService;

    @Autowired
    private EPermitKeyStore keyStore;

    @Container
    public static PostgreSQLContainer<PermitPostgresContainer> postgreSQLContainer = PermitPostgresContainer
            .getInstance();

    @Test
    @SneakyThrows
    void keysTest() {
        KeyStore keystore = KeyStore.getInstance("PKCS12");

        File file = ResourceUtils.getFile("classpath:keystore");
        FileInputStream fis = new FileInputStream(file);
        keystore.load(fis, "123456".toCharArray());
        fis.close();
        List<PublicJwk> jwks = new ArrayList<>();
        Enumeration<String> aliases = keystore.aliases();

        while (aliases.hasMoreElements()) {
            String keyId = aliases.nextElement();
            X509Certificate certificate = (X509Certificate) keystore.getCertificate(keyId);

            JWK jwk = ECKey.parse(certificate).toPublicJWK();
            PublicJwk pjwk = new PublicJwk();
            pjwk.setCrv("secp256k1");
            pjwk.setAlg("ES256K");
            pjwk.setKid(keyId);
            pjwk.setKty("EC");
            pjwk.setUse("sig");
            pjwk.setX(jwk.getRequiredParams().get("x").toString());
            pjwk.setY(jwk.getRequiredParams().get("y").toString());

            jwks.add(pjwk);
        }
        System.out.println(jwks.get(0));
    }

    @Test
    @SneakyThrows
    void jwsTest() {
        Map<String, String> claims = new HashMap<>();
        // claims.put("event_producer", "TR");
        claims.put("event_consumer", "UA");
        String jws = jwsService.createJws("epermit-key-1", claims);
        JWSObject jwsObject = JWSObject.parse(jws);
        PublicJwk jwk = keyStore.getKeys().stream()
                .filter(x -> x.getKid().equals("epermit-key-1"))
                .findFirst()
                .get();
        ECKey key = ECKey.parse(GsonUtil.getGson().toJson(jwk)).toPublicJWK();

        JWSVerifier verifier = new ECDSAVerifier(key);
        Boolean isValid = jwsObject.verify(verifier);
        System.out.println(jws);
        System.out.println(isValid);

    }
    @Test
    @SneakyThrows
    void getKeysTest(){
        
    }

}

/*
 * JWK jwk2 = new ECKeyGenerator(Curve.SECP256K1)
 * .algorithm(JWSAlgorithm.ES256K)
 * .keyUse(KeyUse.SIGNATURE)
 * .keyID("123")
 * .provider(BouncyCastleProviderSingleton.getInstance())
 * .generate().toPublicJWK();
 * 
 * // JWK j = wk = ECKey.load(keystore, keyId, "123456".toCharArray());
 * jwks.add(jwk);
 */