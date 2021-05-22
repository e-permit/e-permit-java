package epermit.services;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import epermit.entities.Key;
import epermit.events.keycreated.KeyCreatedEventFactory;
import epermit.models.results.CommandResult;
import epermit.repositories.AuthorityRepository;
import epermit.repositories.KeyRepository;
import epermit.utils.KeyUtil;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KeyService {
    private final KeyRepository keyRepository;
    private final AuthorityRepository authorityRepository;
    private final KeyUtil keyUtil;
    private final KeyCreatedEventFactory factory;

    @Transactional
    public void seed() {
        Long keyCount = keyRepository.count();
        if (keyCount == 0) {
            Key key = keyUtil.create("1");
            keyRepository.save(key);
        }
    }

    @Transactional
    public void create(String keyId) {
        Optional<Key> keyR = keyRepository.findOneByKeyId(keyId);
        if (keyR.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "KEYID_EXIST");
        }
        Key key = keyUtil.create(keyId);
        keyRepository.save(key);
    }

    @Transactional
    public void enable(Integer id) {
        Long date = OffsetDateTime.now(ZoneOffset.UTC).toEpochSecond();
        Optional<Key> keyR = keyRepository.findById(id);
        if (!keyR.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND , "KEY_NOTFOUND");
        }
        Optional<Key> currentKeyR = keyRepository.findOneByActiveTrue();
        if (currentKeyR.isPresent()) {
            Key currentKey = currentKeyR.get();
            currentKey.setActive(false);
            currentKey.setValidUntil(date);
            keyRepository.save(currentKey);
        }
        Key key = keyR.get();
        key.setActive(true);
        key.setValidFrom(date);
        keyRepository.save(key);

        authorityRepository.findAll().forEach(a -> {
            factory.create(key, a.getCode());
        });
    }

}
