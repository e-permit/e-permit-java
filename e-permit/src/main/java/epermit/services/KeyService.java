package epermit.services;

import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import epermit.entities.Key;
import epermit.events.keycreated.KeyCreatedEventFactory;
import epermit.repositories.AuthorityRepository;
import epermit.repositories.KeyRepository;
import epermit.utils.KeyUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class KeyService {
    private final KeyRepository keyRepository;
    private final AuthorityRepository authorityRepository;
    private final KeyUtil keyUtil;
    private final KeyCreatedEventFactory factory;

    @Transactional
    public void create(String keyId) {
        log.info("KeyService create started {}", keyId);
        Optional<Key> keyR = keyRepository.findOneByKeyId(keyId);
        if (keyR.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "KEYID_EXIST");
        }
        Key key = keyUtil.create(keyId);
        log.info("KeyService create finished {}", key.getPublicJwk());
        keyRepository.save(key);
    }

    @Transactional
    public void enable(Integer id) {
        log.info("KeyService enable started {}", id);
        Optional<Key> keyR = keyRepository.findById(id);
        if (!keyR.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "KEY_NOTFOUND");
        }
        Key key = keyR.get();
        key.setEnabled(true);
        keyRepository.save(key);

        authorityRepository.findAll().forEach(a -> {
            factory.create(key, a.getCode());
        });
    }

    @Transactional
    public void delete(Integer id) {
        log.info("KeyService delete started {}", id);
        Optional<Key> keyR = keyRepository.findById(id);
        if (!keyR.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "KEY_NOTFOUND");
        }
        List<Key> keys = keyRepository.findAllByEnabledTrue();
        if(keys.size() == 1){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "THERE_IS_ONLY_ONE_KEY");
        }
        Key key = keyR.get();
        keyRepository.delete(key);

        authorityRepository.findAll().forEach(a -> {
            factory.create(key, a.getCode());
        });
    }
}
