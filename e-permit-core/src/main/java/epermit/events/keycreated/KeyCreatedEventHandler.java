package epermit.events.keycreated;

import java.text.ParseException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import com.nimbusds.jose.jwk.ECKey;
import org.springframework.stereotype.Service;
import epermit.common.JsonUtil;
import epermit.entities.Authority;
import epermit.entities.AuthorityKey;
import epermit.events.EventHandleResult;
import epermit.events.EventHandler;
import epermit.repositories.AuthorityRepository;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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
        EventHandleResult validation = validate(e);
        if (!validation.isSucceed()) {
            return validation;
        }
        Authority authority = repository.findOneByCode(e.getIssuer()).get();
        List<AuthorityKey> keys = authority.getKeys();

        if (keys.stream().anyMatch(k -> k.getKeyId().equals(e.getKeyId()))) {
            return EventHandleResult.fail("KEYID_EXIST");
        }
        keys.forEach(k -> {
            if (k.getValidUntil() == null) {
                k.setValidUntil(e.getValidFrom());
            }
        });
        AuthorityKey key = new AuthorityKey();
        key.setAuthority(authority);
        key.setKeyId(e.getKeyId());
        key.setJwk(e.getJwk());
        key.setValidFrom(e.getValidFrom());
        key.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC));
        authority.addKey(key);
        repository.save(authority);
        return EventHandleResult.success();
    }

    private EventHandleResult validate(KeyCreatedEvent e) {
        try {
            ECKey key = ECKey.parse(e.getJwk()).toPublicJWK();
            if (!key.getKeyID().equals(e.getKeyId())) {
                log.info("The key in jwk doesnt match with event");
                return EventHandleResult.fail("INVALID_KID");
            }
        } catch (ParseException ex) {
            log.error(ex.getMessage(), ex);
            return EventHandleResult.fail("INVALID_KEY");
        }
        return EventHandleResult.success();
    }
}
