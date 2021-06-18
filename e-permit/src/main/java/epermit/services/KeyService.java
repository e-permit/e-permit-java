package epermit.services;

import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import epermit.ledgerevents.LedgerEventUtil;
import epermit.ledgerevents.keycreated.KeyCreatedLedgerEvent;
import epermit.models.EPermitProperties;
import epermit.entities.PrivateKey;
import epermit.repositories.AuthorityRepository;
import epermit.repositories.PrivateKeyRepository;
import epermit.utils.PrivateKeyUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class KeyService {
    private final PrivateKeyRepository keyRepository;
    private final PrivateKeyUtil keyUtil;
    private final AuthorityRepository authorityRepository;
    private final EPermitProperties properties;
    private final LedgerEventUtil eventUtil;

    @Transactional
    public void create(String keyId) {
        log.info("KeyService create started {}", keyId);
        Optional<PrivateKey> keyR = keyRepository.findOneByKeyId(keyId);
        if (keyR.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "KEYID_EXIST");
        }
        PrivateKey key = keyUtil.create(keyId);
        log.info("KeyService create finished {}", key.getKeyId());
        keyRepository.save(key);
    }

    @Transactional
    public void enable(Integer id) {
        log.info("KeyService enable started {}", id);
        Optional<PrivateKey> keyR = keyRepository.findById(id);
        if (!keyR.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "KEY_NOTFOUND");
        }
        PrivateKey key = keyR.get();
        key.setEnabled(true);
        keyRepository.save(key);

        authorityRepository.findAll().forEach(a -> {
            String prevEventId = eventUtil.getPreviousEventId(a.getCode());
            KeyCreatedLedgerEvent event = new KeyCreatedLedgerEvent(properties.getIssuerCode(), a.getCode(),
                    prevEventId);
            // event.setJwk(key.get);
            // persist and publish
        });
    }

    @Transactional
    public void delete(Integer id) {
        log.info("KeyService delete started {}", id);
        Optional<PrivateKey> keyR = keyRepository.findById(id);
        if (!keyR.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "KEY_NOTFOUND");
        }
        List<PrivateKey> keys = keyRepository.findAllByEnabledTrue();
        if (keys.size() == 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "THERE_IS_ONLY_ONE_KEY");
        }
        PrivateKey key = keyR.get();
        keyRepository.delete(key);

        authorityRepository.findAll().forEach(a -> {
            // persist and publish
        });
    }
}
