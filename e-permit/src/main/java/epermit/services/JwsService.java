package epermit.services;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.ECDSAVerifier;
import com.nimbusds.jose.jwk.ECKey;

import epermit.commons.GsonUtil;
import epermit.entities.LedgerPublicKey;
import epermit.models.EPermitProperties;
import epermit.repositories.LedgerPublicKeyRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwsService {
    private final EPermitProperties properties;
    private final LedgerPublicKeyRepository publicKeyRepository;
    private final EPermitKeyStore keyStore;

    @SneakyThrows
    public <T> String createJws(T payloadObj) {
        LedgerPublicKey publicKey = publicKeyRepository
                .findAllByPartnerAndRevokedFalse(properties.getIssuerCode()).get(0);
        return createJws(publicKey.getKeyId(), payloadObj);
    }

    @SneakyThrows
    public <T> String createJws(String keyId, T payloadObj) {
        log.info("createJws invoked with {} and key_id {}", payloadObj, keyId);
        Gson gson = GsonUtil.getGson();
        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.ES256).keyID(keyId).build();
        Payload payload = new Payload(gson.toJson(payloadObj));
        String jws = keyStore.sign(keyId, payload, header);
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

        Optional<LedgerPublicKey> k = publicKeyRepository.findOneByPartnerAndKeyId(producer, keyId);
        if (!k.isPresent()) {
            log.info("The key doesn't found");
            return false;
        } else if (k.get().isRevoked()) {
            log.info("The key is revoked");
            return false;
        }
        log.info("Key jwk {}", k.get().getJwk());
        ECKey key = ECKey.parse(k.get().getJwk()).toPublicJWK();
        
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
}
