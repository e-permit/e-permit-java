package epermit.utils;

import java.util.Optional;
import com.google.gson.Gson;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.ECDSASigner;
import com.nimbusds.jose.crypto.ECDSAVerifier;
import com.nimbusds.jose.jwk.ECKey;
import org.springframework.stereotype.Component;
import epermit.entities.AuthorityKey;
import epermit.models.EPermitProperties;
import epermit.models.results.JwsValidationResult;
import epermit.repositories.AuthorityKeyRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwsUtil {
    private final KeyUtil keyUtil;
    private final EPermitProperties properties;
    private final AuthorityKeyRepository authorityKeyRepository;

    @SneakyThrows
    public <T> String createJws(T payloadObj) {
        ECKey key = keyUtil.getKey();
        return createJws(key, payloadObj);
    }

    @SneakyThrows
    public <T> String createJws(ECKey key, T payloadObj) {
        Gson gson = GsonUtil.getGson();
        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.ES256).keyID(key.getKeyID()).build();
        Payload payload = new Payload(gson.toJson(payloadObj));
        JWSObject jwsObject = new JWSObject(header, payload);
        JWSSigner signer = new ECDSASigner(key);
        jwsObject.sign(signer);
        return jwsObject.serialize();
    }

    @SneakyThrows
    public JwsValidationResult validateJws(String jws) {
        String issuedFor = getClaim(jws, "issued_for");
        if (!issuedFor.equals(properties.getIssuerCode())) {
            log.info("The jws is not issued for the current authority");
            return JwsValidationResult.fail("INVALID_ISSUED_FOR");
        }
        String issuer = getClaim(jws, "issuer");
        JWSObject jwsObject = JWSObject.parse(jws);
        String keyId = jwsObject.getHeader().getKeyID();
        Optional<AuthorityKey> k  = authorityKeyRepository.findOneByIssuerAndKeyId(issuer, keyId);
        if (!k.isPresent()) {
            log.info("The issuer is not known");
            return JwsValidationResult.fail("INVALID_KEYID");
        }
        ECKey key = ECKey.parse(k.get().getJwk()).toPublicJWK();
        JWSVerifier verifier = new ECDSAVerifier(key);
        Boolean valid = jwsObject.verify(verifier);
        if (!valid) {
            return JwsValidationResult.fail("INVALID_JWS");
        }
        return JwsValidationResult.success(jwsObject.getPayload().toJSONObject());
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    public <T> T getClaim(String jws, String key) {
        JWSObject jwsObject = JWSObject.parse(jws);
        return (T) jwsObject.getPayload().toJSONObject().get(key);
    }
}
