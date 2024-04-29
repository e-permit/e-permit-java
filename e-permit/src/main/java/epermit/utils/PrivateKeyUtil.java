package epermit.utils;

import com.nimbusds.jose.Algorithm;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.gen.ECKeyGenerator;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.stereotype.Component;

import epermit.commons.EpermitValidationException;
import epermit.commons.ErrorCodes;
import epermit.entities.Key;
import epermit.models.EPermitProperties;
import epermit.models.dtos.KeyDto;
import epermit.repositories.KeyRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class PrivateKeyUtil {
    private final EPermitProperties properties;
    private final KeyRepository keyRepository;

    @SneakyThrows
    public KeyDto create(String keyId) {
        ECKey key = new ECKeyGenerator(Curve.P_256).algorithm(Algorithm.parse("ES256"))
                .keyUse(KeyUse.SIGNATURE).keyID(keyId).generate();
        return create(key);
    }

    @SneakyThrows
    public KeyDto create(ECKey key) {
        final String salt = KeyGenerators.string().generateKey();
        TextEncryptor encryptor = Encryptors.text(properties.getKeystorePassword(), salt);
        String encryptedJwk = encryptor.encrypt(key.toJSONString());
        KeyDto k = new KeyDto();
        k.setKeyId(key.getKeyID());
        k.setSalt(salt);
        k.setPrivateJwk(encryptedJwk);
        k.setJwk(key.toPublicJWK().toJSONString());
        log.info("Key created jwk: {}, salt: {}", key.toPublicJWK().toJSONString(), salt);
        return k;
    }
}
