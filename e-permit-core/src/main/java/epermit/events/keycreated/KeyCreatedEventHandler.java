package epermit.events.keycreated;

import java.text.ParseException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
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

@Service("KEY_CREATED")
@Slf4j
public class KeyCreatedEventHandler implements EventHandler {

    private final AuthorityRepository repository;

    public KeyCreatedEventHandler(AuthorityRepository repository) {
        this.repository = repository;
    }

    @SneakyThrows
    @Override
    public EventHandleResult handle(String payload) {
        KeyCreatedEvent e = JsonUtil.getGson().fromJson(payload, KeyCreatedEvent.class);
        Boolean valid = validate(e);
        if (!valid) {
            return EventHandleResult.fail("INVALID_EVENT");
        }
        Authority authority = repository.findByCode(e.getIssuer()).get();
        authority.getKeys().forEach(k -> {
            if (k.getValidTo() == null) {
                k.setValidTo(e.getValidFrom());
            }
        });
        AuthorityKey key = new AuthorityKey();
        key.setAuthority(authority);
        key.setKid(e.getKeyId());
        key.setJwk(e.getJwk());
        key.setValidFrom(e.getValidFrom());
        key.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC));
        authority.addKey(key);
        repository.save(authority);
        return EventHandleResult.success();
    }

    private Boolean validate(KeyCreatedEvent e) {
        try {
            ECKey key = ECKey.parse(e.getJwk()).toPublicJWK();
            if (!key.getKeyID().equals(e.getKeyId())) {
                log.info("INVALID_KID");
                log.info("The key in jwk doesnt match with event");
                return false;
            }
        } catch (ParseException ex) {
            log.info("INVALID_JWK");
            log.error(ex.getMessage(), ex);
            return false;
        }
        return true;
    }
}
