package epermit.services;

import java.util.Base64;
import java.util.List;
import java.util.Optional;
import com.nimbusds.jose.jwk.ECKey;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import epermit.ledgerevents.LedgerEventUtil;
import epermit.ledgerevents.keycreated.KeyCreatedLedgerEvent;
import epermit.ledgerevents.keyrevoked.KeyRevokedLedgerEvent;
import epermit.models.EPermitProperties;
import epermit.commons.GsonUtil;
import epermit.entities.LedgerPublicKey;
import epermit.entities.PrivateKey;
import epermit.repositories.AuthorityRepository;
import epermit.repositories.LedgerPublicKeyRepository;
import epermit.repositories.PrivateKeyRepository;
import epermit.utils.PrivateKeyUtil;
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
            if (privateKey != null) {
                String jwkStr = new String(Base64.getUrlDecoder().decode(privateKey));
                log.info("Private key exist");
                key = keyUtil.create(ECKey.parse(jwkStr));
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
        keyRepository.save(keyEntity);
        authorityRepository.findAll().forEach(authority -> {
            String prevEventId = eventUtil.getPreviousEventId(authority.getCode());
            KeyCreatedLedgerEvent event = new KeyCreatedLedgerEvent(properties.getIssuerCode(),
                    authority.getCode(), prevEventId);
            event.setKid(keyId);

        });
        log.info("KeyService create finished {}", key.getKeyId());

    }

    @Transactional
    public void delete(Integer id) {
        log.info("KeyService delete started {}", id);
        Optional<PrivateKey> keyR = keyRepository.findById(id);
        if (!keyR.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "KEY_NOTFOUND");
        }
        PrivateKey key = keyR.get();
        keyRepository.delete(key);

        authorityRepository.findAll().forEach(authority -> {
            // KeyRevokedLedgerEvent event =
        });
    }
}
