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
import epermit.models.EPermitProperties;
import epermit.models.dtos.PrivateKey;
import epermit.repositories.PrivateKeyRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class PrivateKeyUtil {
    private final EPermitProperties properties;
    private final PrivateKeyRepository keyRepository;

    @SneakyThrows
    public PrivateKey create(String keyId) {
        ECKey key = new ECKeyGenerator(Curve.P_256).algorithm(Algorithm.parse("ES256"))
                .keyUse(KeyUse.SIGNATURE).keyID(keyId).generate();
        return create(key);
    }

    @SneakyThrows
    public PrivateKey create(ECKey key) {
        final String salt = KeyGenerators.string().generateKey();
        TextEncryptor encryptor = Encryptors.text(properties.getKeyPassword(), salt);
        String encryptedJwk = encryptor.encrypt(key.toJSONString());
        PrivateKey k = new PrivateKey();
        k.setKeyId(key.getKeyID());
        k.setSalt(salt);
        k.setPrivateJwk(encryptedJwk);
        k.setPublicJwk(key.toPublicJWK().toJSONString());
        log.info("Key created jwk: {}, salt: {}", key.toPublicJWK().toJSONString(), salt);
        return k;
    }

    @SneakyThrows
    public ECKey getKey() {
        epermit.entities.PrivateKey privateKey =
                keyRepository.findFirstByEnabledTrueOrderByIdDesc();
        TextEncryptor decryptor =
                Encryptors.text(properties.getKeyPassword(), privateKey.getSalt());
        ECKey key = ECKey.parse(decryptor.decrypt(privateKey.getPrivateJwk()));
        return key;
    }
}


