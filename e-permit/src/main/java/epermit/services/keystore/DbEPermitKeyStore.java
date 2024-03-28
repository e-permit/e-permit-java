package epermit.services.keystore;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Service;

import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.ECDSASigner;
import com.nimbusds.jose.jwk.ECKey;

import epermit.commons.GsonUtil;
import epermit.entities.Key;
import epermit.models.dtos.PublicJwk;
import epermit.repositories.KeyRepository;
import epermit.services.EPermitKeyStore;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "epermit.keystore.db", name = { "password" })
public class DbEPermitKeyStore implements EPermitKeyStore {
    @Value("${epermit.keystore.db.password:}")
    String password;

    private final KeyRepository keyRepository;

    @Override
    @SneakyThrows
    public String sign(String keyId, Payload payload, JWSHeader header) {
        Key privateKey = keyRepository.findOneByKeyId(keyId).orElseThrow();
        TextEncryptor decryptor = Encryptors.text(password, privateKey.getSalt());
        ECKey key = ECKey.parse(decryptor.decrypt(privateKey.getPrivateJwk()));
        
        JWSObject jwsObject = new JWSObject(header, payload);
        JWSSigner signer = new ECDSASigner(key);
        jwsObject.sign(signer);
        return jwsObject.serialize();
    }

    @Override
    @SneakyThrows
    public List<PublicJwk> getKeys() {
        List<PublicJwk> jwks = new ArrayList<>();
        for(Key privateKey: keyRepository.findAll()){
            TextEncryptor decryptor = Encryptors.text(password, privateKey.getSalt());
            ECKey key = ECKey.parse(decryptor.decrypt(privateKey.getPrivateJwk()));
            PublicJwk jwk = GsonUtil.getGson().fromJson(key.toPublicJWK().toJSONString(), PublicJwk.class);
            jwks.add(jwk);
        }
        return jwks;
    }

}
