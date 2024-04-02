package epermit.services;

import java.time.Instant;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;
import com.nimbusds.jose.jwk.ECKey;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import epermit.ledgerevents.LedgerEventUtil;
import epermit.ledgerevents.keycreated.KeyCreatedLedgerEvent;
import epermit.ledgerevents.keyrevoked.KeyRevokedLedgerEvent;
import epermit.models.EPermitProperties;
import epermit.models.dtos.PublicJwk;
import epermit.commons.GsonUtil;
import epermit.entities.LedgerPublicKey;
import epermit.entities.PrivateKey;
import epermit.repositories.AuthorityRepository;
import epermit.repositories.LedgerPublicKeyRepository;
import epermit.repositories.PrivateKeyRepository;
import epermit.utils.PrivateKeyUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PrivateKeyService {
    private final PrivateKeyRepository keyRepository;
    private final PrivateKeyUtil keyUtil;
    private final AuthorityRepository authorityRepository;
    private final EPermitProperties properties;
    private final LedgerEventUtil eventUtil;
    private final LedgerPublicKeyRepository ledgerPublicKeyRepository;

    @Transactional
    @SneakyThrows
    public void seed() {
        Long keyCount = keyRepository.count();
        if (keyCount == 0) {
            epermit.models.dtos.PrivateKey key;
            String privateKey = properties.getIssuerPrivateKey();
            if (privateKey != null && !privateKey.isBlank()) {
                // if an encrypted key is provided via env, use it
                try {
                    String jwkStr = new String(Base64.getUrlDecoder().decode(privateKey));
                    log.info("Private key exist");
                    key = keyUtil.create(ECKey.parse(jwkStr));
                } catch (Exception ex) {
                    key = keyUtil.create("1");
                }
            } else {
                key = keyUtil.create("1");
            }
            PrivateKey keyEntity = new epermit.entities.PrivateKey();
            keyEntity.setKeyId(key.getKeyId());
            keyEntity.setPrivateJwk(key.getPrivateJwk());
            keyEntity.setSalt(key.getSalt());
            keyEntity.setEnabled(true);
            keyRepository.save(keyEntity);
            LedgerPublicKey publicKey = new LedgerPublicKey();
            publicKey.setJwk(key.getPublicJwk());
            publicKey.setKeyId(key.getKeyId());
            publicKey.setAuthorityCode(properties.getIssuerCode());
            ledgerPublicKeyRepository.save(publicKey);
        }
    }

    @Transactional
    public void create(String keyId) {
        log.info("KeyService create started {}", keyId);
        epermit.models.dtos.PrivateKey key = keyUtil.create(keyId);
        PrivateKey keyEntity = new PrivateKey();
        keyEntity.setKeyId(key.getKeyId());
        keyEntity.setPrivateJwk(key.getPrivateJwk());
        keyEntity.setSalt(key.getSalt());
        keyEntity.setEnabled(true);
        keyRepository.save(keyEntity);
        authorityRepository.findAll().forEach(authority -> {
            String prevEventId = eventUtil.getPreviousEventId(authority.getCode());
            KeyCreatedLedgerEvent event = new KeyCreatedLedgerEvent(properties.getIssuerCode(),
                    authority.getCode(), prevEventId);
            event.setKid(keyId);
            PublicJwk jwk = GsonUtil.getGson().fromJson(key.getPublicJwk(), PublicJwk.class);
            event.setAlg(jwk.getAlg());
            event.setCrv(jwk.getCrv());
            event.setKty(jwk.getKty());
            event.setUse(jwk.getUse());
            event.setX(jwk.getX());
            event.setY(jwk.getY());
            eventUtil.persistAndPublishEvent(event, false);
        });
        log.info("KeyService create finished {}", key.getKeyId());

    }

    @Transactional
    public void delete(UUID id) {
        log.info("KeyService delete started {}", id);
        PrivateKey key = keyRepository.findById(id)
          .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "KEY_NOTFOUND"));
        keyRepository.delete(key);

        authorityRepository.findAll().forEach(authority -> {
            String prevEventId = eventUtil.getPreviousEventId(authority.getCode());
            KeyRevokedLedgerEvent event = new KeyRevokedLedgerEvent(properties.getIssuerCode(),
                    authority.getCode(), prevEventId);
            event.setKeyId(key.getKeyId());
            event.setRevokedAt(Instant.now().getEpochSecond());
            eventUtil.persistAndPublishEvent(event, false);
        });
    }
}
