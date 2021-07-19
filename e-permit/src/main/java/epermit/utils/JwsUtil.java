package epermit.utils;

import java.util.Map;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import epermit.commons.GsonUtil;
import epermit.entities.LedgerPublicKey;
import epermit.models.EPermitProperties;
import epermit.models.results.JwsValidationResult;
import epermit.repositories.LedgerPublicKeyRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwsUtil {
    private final PrivateKeyUtil keyUtil;
    private final EPermitProperties properties;
    private final LedgerPublicKeyRepository publicKeyRepository;

    @SneakyThrows
    public <T> String createJws(T payloadObj) {
        ECKey key = keyUtil.getKey();
        return createJws(key, payloadObj);
    }

    @SneakyThrows
    public <T> String createJws(ECKey key, T payloadObj) {
        log.info("createJws started with {} and key_id {}", payloadObj, key.getKeyID());
        Gson gson = GsonUtil.getGson();
        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.ES256).keyID(key.getKeyID()).build();
        Payload payload = new Payload(gson.toJson(payloadObj));
        JWSObject jwsObject = new JWSObject(header, payload);
        JWSSigner signer = new ECDSASigner(key);
        jwsObject.sign(signer);
        String jws = jwsObject.serialize();
        log.info("createJws ended with {}", jws);
        return jws;
    }

    @SneakyThrows
    public JwsValidationResult validateJws(String jws) {
        String issuedFor = getClaim(jws, "issued_for");
        log.info("Jws validation issued_for {}", issuedFor);
        if (!issuedFor.equals(properties.getIssuerCode())) {
            log.info("The jws is not issued for the current authority {}", jws);
            return JwsValidationResult.fail("INVALID_ISSUED_FOR");
        }
        String issuer = getClaim(jws, "issuer");
        JWSObject jwsObject = JWSObject.parse(jws);
        String keyId = jwsObject.getHeader().getKeyID();

        Optional<LedgerPublicKey> k =
                publicKeyRepository.findOneByAuthorityCodeAndKeyId(issuer, keyId);
        if (!k.isPresent()) {
            log.info("The issuer or key doesn't found");
            return JwsValidationResult.fail("INVALID_KEYID");
        }else if(k.get().isRevoked()){
            log.info("The key is revoked");
            return JwsValidationResult.fail("REVOKED_KEY");
        }
        ECKey key = ECKey.parse(k.get().getJwk()).toPublicJWK();
        JWSVerifier verifier = new ECDSAVerifier(key);
        Boolean valid = jwsObject.verify(verifier);
        if (!valid) {
            log.info("Invalid jws");
            return JwsValidationResult.fail("INVALID_JWS");
        }
        log.info("Jws validation succeed {}", jwsObject.getPayload().toJSONObject());
        return JwsValidationResult.success(jwsObject.getPayload().toJSONObject());
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    public <T> T getClaim(String jws, String key) {
        JWSObject jwsObject = JWSObject.parse(jws);
        return (T) jwsObject.getPayload().toJSONObject().get(key);
    }

    @SneakyThrows
    public Map<String, Object> resolveJws(HttpHeaders headers) {
        String header = headers.getFirst(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith("Bearer ")) {
            throw new Exception("No JWT token found in request headers");
        }

        String jws = header.substring(7);
        JwsValidationResult r = validateJws(jws);
        if (!r.isValid()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED");
        }
        return r.getPayload();
    }

    public HttpHeaders getJwsHeader(String jws) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + jws);
        return headers;
    }
}
