package epermit.services;

import java.time.Instant;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import epermit.commons.EpermitValidationException;
import epermit.commons.ErrorCodes;
import epermit.entities.Key;
import epermit.ledgerevents.LedgerEventUtil;
import epermit.ledgerevents.keycreated.KeyCreatedLedgerEvent;
import epermit.ledgerevents.keyrevoked.KeyRevokedLedgerEvent;
import epermit.models.EPermitProperties;
import epermit.models.dtos.KeyDto;
import epermit.repositories.AuthorityRepository;
import epermit.repositories.KeyRepository;
import epermit.utils.PrivateKeyUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class KeyService {
    private final AuthorityRepository authorityRepository;
    private final KeyRepository keyRepository;
    private final PrivateKeyUtil privateKeyUtil;
    private final EPermitProperties properties;
    private final LedgerEventUtil eventUtil;

    @Transactional
    @SneakyThrows
    public void seed() {
        Long keyCount = keyRepository.count();
        if (keyCount == 0) {
            KeyDto keyInput = privateKeyUtil.create("1");
            Key keyEntity = new Key();
            keyEntity.setKeyId(keyInput.getKeyId());
            keyEntity.setJwk(keyInput.getJwk());
            keyEntity.setPrivateJwk(keyInput.getPrivateJwk());
            keyEntity.setSalt(keyInput.getSalt());
            keyRepository.save(keyEntity);
        }
    }

    @Transactional
    public void create(String keyId) {
        log.info("KeyService create started {}", keyId);

        if (keyRepository.findOneByKeyId(keyId).isPresent()) {
            throw new EpermitValidationException(ErrorCodes.KEYID_ALREADY_EXISTS);
        }
        KeyDto key = privateKeyUtil.create(keyId);
        Key keyEntity = new Key();
        keyEntity.setKeyId(key.getKeyId());
        keyEntity.setJwk(key.getJwk());
        keyEntity.setPrivateJwk(key.getPrivateJwk());
        keyEntity.setSalt(key.getSalt());
        keyRepository.save(keyEntity);
        authorityRepository.findAll().forEach(authority -> {
            String prevEventId = eventUtil.getPreviousEventId(authority.getCode());
            KeyCreatedLedgerEvent event = new KeyCreatedLedgerEvent(properties.getIssuerCode(),
                    authority.getCode(), prevEventId);
            event.setAuthority(properties.getIssuerCode());
            event.setKid(key.getKeyId());
            event.setJwk(key.getJwk());
            eventUtil.persistAndPublishEvent(event);
        });
        log.info("KeyService create finished {}", key.getKeyId());

    }

    @Transactional
    public void revoke(String keyId) {
        log.info("KeyService delete started {}", keyId);
        if(keyRepository.findAllByRevokedFalse().size() < 2){
            throw new EpermitValidationException(ErrorCodes.INSUFFICIENT_KEY);
        }
        Key key = keyRepository.findOneByKeyIdAndRevokedFalse(keyId)
                .orElseThrow(() -> new EpermitValidationException(ErrorCodes.KEY_NOTFOUND));
        key.setRevoked(true);
        key.setRevokedAt(Instant.now().getEpochSecond());
        authorityRepository.findAll().forEach(authority -> {
            String prevEventId = eventUtil.getPreviousEventId(authority.getCode());
            KeyRevokedLedgerEvent event = new KeyRevokedLedgerEvent(properties.getIssuerCode(),
                    authority.getCode(), prevEventId);
            event.setAuthority(properties.getIssuerCode());
            event.setKeyId(keyId);
            event.setRevokedAt(Instant.now().getEpochSecond());
            eventUtil.persistAndPublishEvent(event);
        });
    }
}
