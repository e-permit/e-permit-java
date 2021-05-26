package epermit.events.keycreated;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import org.springframework.stereotype.Service;
import epermit.entities.Authority;
import epermit.entities.AuthorityKey;
import epermit.events.EventHandler;
import epermit.repositories.AuthorityRepository;
import lombok.RequiredArgsConstructor;

@Service("KEY_CREATED_EVENT_HANDLER")
@RequiredArgsConstructor
public class KeyCreatedEventHandler implements EventHandler {
    private final AuthorityRepository authorityRepository;

    public void handle(Object obj) {
        KeyCreatedEvent e = (KeyCreatedEvent)obj;
        Authority authority = authorityRepository.findOneByCode(e.getIssuer()).get();
        List<AuthorityKey> keys = authority.getKeys();
        keys.forEach(k -> {
            k.setActive(false);
        });
        AuthorityKey key = new AuthorityKey();
        key.setAuthority(authority);
        key.setKeyId(e.getKeyId());
        key.setJwk(e.getJwk());
        key.setValidFrom(e.getValidFrom());
        key.setActive(true);
        authority.addKey(key);
        authorityRepository.save(authority);
    }
}
