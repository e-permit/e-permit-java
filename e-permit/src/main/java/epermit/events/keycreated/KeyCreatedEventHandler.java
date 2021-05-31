package epermit.events.keycreated;

import java.util.List;
import org.springframework.stereotype.Service;
import epermit.entities.Authority;
import epermit.entities.AuthorityKey;
import epermit.events.EventHandler;
import epermit.repositories.AuthorityRepository;
import epermit.utils.GsonUtil;
import lombok.RequiredArgsConstructor;

@Service("KEY_CREATED_EVENT_HANDLER")
@RequiredArgsConstructor
public class KeyCreatedEventHandler implements EventHandler {
    private final AuthorityRepository authorityRepository;

    public void handle(Object obj) {
        KeyCreatedEvent e = (KeyCreatedEvent)obj;
        Authority authority = authorityRepository.findOneByCode(e.getIssuer()).get();
        AuthorityKey key = new AuthorityKey();
        key.setAuthority(authority);
        key.setKeyId(e.getJwk().getKid());
        key.setJwk(GsonUtil.getGson().toJson(e.getJwk()));
        authority.addKey(key);
        authorityRepository.save(authority);
    }
}
