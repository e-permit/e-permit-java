package epermit.utils;

import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.ECDSAVerifier;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.crypto.ECDSASigner;
import com.nimbusds.jose.JWSSigner;

import epermit.commons.EpermitValidationException;
import epermit.commons.ErrorCodes;
import epermit.commons.GsonUtil;
import epermit.entities.Authority;
import epermit.entities.AuthorityKey;
import epermit.entities.Key;
import epermit.models.EPermitProperties;
import epermit.repositories.AuthorityRepository;
import epermit.repositories.KeyRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwsUtil {
    private final EPermitProperties properties;
    private final AuthorityRepository authorityRepository;
    private final KeyRepository keyRepository;

    @SneakyThrows
    public <T> String createJws(T payloadObj) {
        Key key = keyRepository.findFirstByRevokedFalseOrderByCreatedAtAsc()
                .orElseThrow(() -> new EpermitValidationException(ErrorCodes.KEY_NOTFOUND));
        return createJws(key.getKeyId(), payloadObj);
    }

    @SneakyThrows
    public <T> String createJws(String keyId, T payloadObj) {
        log.info("createJws invoked with {} and key_id {}", payloadObj, keyId);
        Gson gson = GsonUtil.getGson();
        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.ES256)
                .keyID(keyId)
                .customParam("authority", properties.getIssuerCode())
                .build();
        Payload payload = new Payload(gson.toJson(payloadObj));
        String jws = sign(keyId, payload, header);
        log.info("createJws ended with {}", jws);
        return jws;
    }

    @SneakyThrows
    public Boolean validateJws(String jws) {
        String consumer = getClaim(jws, "event_consumer");
        log.info("Jws validation consumer {}", consumer);
        if (!consumer.equals(properties.getIssuerCode())) {
            log.info("The jws is not consumer the current authority {}", jws);
            return false;
        }
        String producer = getClaim(jws, "event_producer");
        JWSObject jwsObject = JWSObject.parse(jws);
        String keyId = jwsObject.getHeader().getKeyID();
        Authority authority = authorityRepository.findOneByCode(producer)
                .orElseThrow(() -> new EpermitValidationException(ErrorCodes.AUTHORITY_NOT_FOUND));
        AuthorityKey k = authority.getValidKeyById(keyId);

        log.info("Key jwk {}", k.getJwk());
        ECKey key = ECKey.parse(k.getJwk()).toPublicJWK();

        JWSVerifier verifier = new ECDSAVerifier(key);
        Boolean valid = jwsObject.verify(verifier);
        if (!valid) {
            log.info("Invalid jws");
            return false;
        }
        log.info("Jws validation succeed {}", jwsObject.getPayload().toJSONObject());
        return true;
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    public <T> T getClaim(String jws, String key) {
        JWSObject jwsObject = JWSObject.parse(jws);
        return (T) jwsObject.getPayload().toJSONObject().get(key);
    }

    @SneakyThrows
    private String sign(String keyId, Payload payload, JWSHeader header) {
        Key privateKey = keyRepository.findOneByKeyId(keyId).orElseThrow();
        TextEncryptor decryptor = Encryptors.text(properties.getKeystorePassword(), privateKey.getSalt());
        ECKey key = ECKey.parse(decryptor.decrypt(privateKey.getPrivateJwk()));

        JWSObject jwsObject = new JWSObject(header, payload);
        JWSSigner signer = new ECDSASigner(key);
        jwsObject.sign(signer);
        return jwsObject.serialize();
    }
}
