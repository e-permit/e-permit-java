package epermit.services;

import java.security.interfaces.ECPublicKey;
import java.time.OffsetDateTime;
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
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.gen.ECKeyGenerator;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.stereotype.Component;
import epermit.common.JsonUtil;
import epermit.common.JwsValidationResult;
import epermit.common.PermitProperties;
import epermit.entities.Authority;
import epermit.entities.AuthorityKey;
import epermit.entities.Key;
import epermit.repositories.AuthorityRepository;
import epermit.repositories.KeyRepository;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class KeyService {
    private PermitProperties props;
    private KeyRepository repository;
    private AuthorityRepository authorityRepository;

    public KeyService(PermitProperties props, KeyRepository repository, AuthorityRepository authorityRepository) {
        this.props = props;
        this.repository = repository;
        this.authorityRepository = authorityRepository;
    }

    @SneakyThrows
    public Key create(String kid) {
        final String salt = KeyGenerators.string().generateKey();
        TextEncryptor encryptor = Encryptors.text(props.getKeyPassword(), salt);
        ECKey key = new ECKeyGenerator(Curve.P_256).keyUse(KeyUse.SIGNATURE).keyID(kid).generate();
        String jwk = encryptor.encrypt(key.toJSONString());
        Key k = new Key();
        k.setKid(kid);
        k.setCreatedAt(OffsetDateTime.now());
        k.setEnabled(false);
        k.setSalt(salt);
        k.setContent(jwk);
        return k;
    }

    @SneakyThrows
    public ECKey getKey() {
        Key k = repository.findOneByEnabledTrue().get();
        TextEncryptor decryptor = Encryptors.text(props.getKeyPassword(), k.getSalt());
        ECKey key = ECKey.parse(decryptor.decrypt(k.getContent()));
        return key;
    }

    @SneakyThrows
    public <T> String createJws(T payloadObj) {
        Gson gson = JsonUtil.getGson();
        ECKey key = getKey();
        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.ES256).keyID(key.getKeyID()).build();
        Payload payload = new Payload(gson.toJson(payloadObj));
        JWSObject jwsObject = new JWSObject(header, payload);
        JWSSigner signer = new ECDSASigner(key);
        jwsObject.sign(signer);
        return jwsObject.serialize();
    }

    @SneakyThrows
    public JwsValidationResult validateJws(String jws) {
        String issuedFor = JsonUtil.getClaim(jws, "issued_for");
        if (!issuedFor.equals(props.getIssuerCode())) {
            log.info("The jws is not issued for the current authority");
            return JwsValidationResult.fail("INVALID_ISSUED_FOR");
        }
        String issuer = JsonUtil.getClaim(jws, "issuer");
        Optional<Authority> r = authorityRepository.findByCode(issuer);
        if (!r.isPresent()) {
            log.info("The issuer is not known");
            return JwsValidationResult.fail("NOTKNOWN_ISSUER");
        }
        JWSObject jwsObject = JWSObject.parse(jws);
        String kid = jwsObject.getHeader().getKeyID();
        AuthorityKey authorityKey = r.get().getKeys().stream().filter(x -> x.getKid().equals(kid)).findFirst().get();
        ECKey key = ECKey.parse(authorityKey.getJwk()).toPublicJWK();
        JWSVerifier verifier = new ECDSAVerifier(key);
        Boolean valid = jwsObject.verify(verifier);
        if (!valid) {
            return JwsValidationResult.fail("INVALID_JWS");
        }
        return JwsValidationResult.success();
    }
}
