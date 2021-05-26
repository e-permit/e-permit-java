package epermit.utils;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.gen.ECKeyGenerator;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.stereotype.Component;
import epermit.entities.Key;
import epermit.models.EPermitProperties;
import epermit.repositories.KeyRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class KeyUtil {
    private final EPermitProperties properties;
    private final KeyRepository keyRepository;

    @SneakyThrows
    public Key create(String keyId) {
        ECKey key =
                new ECKeyGenerator(Curve.P_256).keyUse(KeyUse.SIGNATURE).keyID(keyId).generate();
        return create(key);
    }

    @SneakyThrows
    public Key create(ECKey key) {
        final String salt = KeyGenerators.string().generateKey();
        TextEncryptor encryptor = Encryptors.text(properties.getKeyPassword(), salt);
        String encryptedJwk = encryptor.encrypt(key.toJSONString());
        Key k = new Key();
        k.setKeyId(key.getKeyID());
        k.setSalt(salt);
        k.setPrivateJwk(encryptedJwk);
        k.setPublicJwk(key.toPublicJWK().toJSONString());
        log.info("Key created jwk: " + k.getPublicJwk());
        return k;
    }

    @SneakyThrows
    public ECKey getKey() {
        Key privateKey = keyRepository.findOneByActiveTrue().get();
        TextEncryptor decryptor =
                Encryptors.text(properties.getKeyPassword(), privateKey.getSalt());
        ECKey key = ECKey.parse(decryptor.decrypt(privateKey.getPrivateJwk()));
        return key;
    }
}


