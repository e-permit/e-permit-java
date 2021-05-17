package epermit.services;

import org.springframework.stereotype.Service;
import epermit.entities.Key;
import epermit.repositories.KeyRepository;
import epermit.utils.KeyUtil;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KeyService {
    private final KeyRepository keyRepository;
    private final KeyUtil keyUtil;

    public void seed() {
        Long keyCount = keyRepository.count();
        if (keyCount == 0) {
            Key key = keyUtil.create("1");
            keyRepository.save(key);
        }
    }
}
