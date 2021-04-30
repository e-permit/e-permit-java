package epermit.events.keycreated;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import epermit.entities.Authority;
import epermit.entities.AuthorityKey;
import epermit.events.EventHandleResult;
import epermit.repositories.AuthorityRepository;
import lombok.SneakyThrows;

public class KeyCreatedEventHandler {

    private final AuthorityRepository repository;

    public KeyCreatedEventHandler(AuthorityRepository repository) {
        this.repository = repository;
    }

    @SneakyThrows
    public EventHandleResult handle(KeyCreatedEvent e) {
        Authority authority = repository.findByCode(e.getIssuer()).get();
        AuthorityKey key = new AuthorityKey();
        key.setAuthority(authority);
        key.setContent(e.getJwk());
        key.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC));
        key.setKid(e.getKeyId());
        authority.addKey(key);
        repository.save(authority);
        return EventHandleResult.success();
    }
}