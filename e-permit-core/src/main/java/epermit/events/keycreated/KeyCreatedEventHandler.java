package epermit.events.keycreated;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.springframework.stereotype.Service;
import epermit.common.JsonUtil;
import epermit.entities.Authority;
import epermit.entities.AuthorityKey;
import epermit.events.EventHandleResult;
import epermit.events.EventHandler;
import epermit.repositories.AuthorityRepository;
import lombok.SneakyThrows;

@Service("KEY_CREATED")
public class KeyCreatedEventHandler implements EventHandler {

    private final AuthorityRepository repository;

    public KeyCreatedEventHandler(AuthorityRepository repository) {
        this.repository = repository;
    }

    @SneakyThrows
    @Override
    public EventHandleResult handle(String payload) {
        KeyCreatedEvent e = JsonUtil.getGson().fromJson(payload, KeyCreatedEvent.class);
        Authority authority = repository.findByCode(e.getIssuer()).get();
        AuthorityKey key = new AuthorityKey();
        key.setAuthority(authority);
        key.setJwk(e.getJwk());
        key.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC));
        key.setKid(e.getKeyId());
        authority.addKey(key);
        repository.save(authority);
        return EventHandleResult.success();
    }
}
