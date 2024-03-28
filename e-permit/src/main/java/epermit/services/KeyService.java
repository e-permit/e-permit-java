package epermit.services;

import java.security.PrivateKey;
import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import epermit.commons.GsonUtil;
import epermit.entities.Key;
import epermit.ledgerevents.LedgerEventUtil;
import epermit.ledgerevents.keycreated.KeyCreatedLedgerEvent;
import epermit.ledgerevents.keyrevoked.KeyRevokedLedgerEvent;
import epermit.models.EPermitProperties;
import epermit.models.dtos.PublicJwk;
import epermit.repositories.AuthorityRepository;
import epermit.repositories.KeyRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class KeyService {
    private final AuthorityRepository authorityRepository;
    private final KeyRepository keyRepository;

    private final EPermitProperties properties;
    private final LedgerEventUtil eventUtil;
    private final EPermitKeyStore keyStore;

    @Transactional
    @SneakyThrows
    public void sync() {
        List<PublicJwk> keys = keyStore.getKeys();
        keys.forEach(jwk -> {
            if (!keyRepository.existsByKeyId(jwk.getKid())) {
                String pubJwk = GsonUtil.getGson().toJson(jwk);
                Key key = new Key();
                key.setKeyId(jwk.getKid());
                key.setJwk(pubJwk);
                keyRepository.save(key);
                authorityRepository.findAll().forEach(authority -> {
                    String prevEventId = eventUtil.getPreviousEventId(authority.getCode());
                    KeyCreatedLedgerEvent event = new KeyCreatedLedgerEvent(properties.getIssuerCode(),
                            authority.getCode(), prevEventId);
                    event.setKid(jwk.getKid());
                    event.setJwk(pubJwk);
                    eventUtil.persistAndPublishEvent(event);
                });
            }
        });
    }

     @Transactional
    public void create(String keyId) {
        log.info("KeyService create started {}", keyId);
        //epermit.models.dtos.PrivateKey key = keyUtil.create(keyId);
        Key keyEntity = new Key();
        keyEntity.setKeyId(key.getKeyId());
        keyEntity.setPrivateJwk(key.getPrivateJwk());
        keyEntity.setSalt(key.getSalt());
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
            eventUtil.persistAndPublishEvent(event);
        });
        log.info("KeyService create finished {}", key.getKeyId());

    }

    @Transactional
    public void revoke(String keyId) {
        log.info("KeyService delete started {}", keyId);

        authorityRepository.findAll().forEach(authority -> {
            String prevEventId = eventUtil.getPreviousEventId(authority.getCode());
            KeyRevokedLedgerEvent event = new KeyRevokedLedgerEvent(properties.getIssuerCode(),
                    authority.getCode(), prevEventId);
            event.setKeyId(keyId);
            event.setRevokedAt(Instant.now().getEpochSecond());
            eventUtil.persistAndPublishEvent(event);
        });
    }
}
