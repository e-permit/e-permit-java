package epermit.services.keystore;

import java.io.FileInputStream;
import java.security.cert.Certificate;
import java.security.Key;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.ECDSASigner;
import com.nimbusds.jose.crypto.bc.BouncyCastleProviderSingleton;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.gen.ECKeyGenerator;

import epermit.models.dtos.PublicJwk;
import epermit.services.EPermitKeyStore;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "epermit.keystore.pkcs12", name = { "file", "password" })
public class Pkcs12EPermitKeyStore implements EPermitKeyStore {
    @Value("${epermit.keystore.pkcs12.file:}")
    String keyStoreFile;

    @Value("${epermit.keystore.pkcs12.password:}")
    String keyStorePassword;

    @Override
    @SneakyThrows
    public String sign(String keyId, Payload payload, JWSHeader header) { 
        KeyStore keystore = getKeyStore();
        JWSObject jwsObject = new JWSObject(header, payload);
        JWSSigner signer = new ECDSASigner(ECKey.load(keystore, keyId, keyStorePassword.toCharArray()));
        jwsObject.sign(signer);
        return jwsObject.serialize();
    }

    @Override
    @SneakyThrows
    public List<PublicJwk> getKeys() {
        KeyStore keystore = getKeyStore();

        List<JWK> jwks = new ArrayList<>();
        Enumeration<String> aliases = keystore.aliases();

        while (aliases.hasMoreElements()) {
            String keyId = aliases.nextElement();
            X509Certificate certificate = (X509Certificate) keystore.getCertificate(keyId);

            JWK jwk = ECKey.parse(certificate).toPublicJWK();
            jwks.add(jwk);
        }
   
        return jwks.stream().map((x)-> PublicJwk.fromJwk(x)).toList();
    }

    @SneakyThrows
    public void generateKeypair(String keyId) {
        KeyStore keystore = getKeyStore();
        ECKey keypair = new ECKeyGenerator(Curve.SECP256K1)
                .keyID(keyId)
                .provider(BouncyCastleProviderSingleton.getInstance())
                .generate();

        keystore.setKeyEntry(keyId, (Key) keypair.toPrivateKey(), keyStorePassword.toCharArray(), new Certificate[] {});
    }

    @SneakyThrows
    private KeyStore getKeyStore(){
        KeyStore keystore = KeyStore.getInstance("PKCS12");
        FileInputStream fis = new FileInputStream(keyStoreFile);
        keystore.load(fis, keyStorePassword.toCharArray());
        fis.close();
        return keystore;
    }

}
