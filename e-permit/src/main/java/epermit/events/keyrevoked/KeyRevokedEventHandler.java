package epermit.events.keyrevoked;

import org.springframework.stereotype.Service;
import epermit.entities.AuthorityKey;
import epermit.events.EventHandler;
import epermit.repositories.AuthorityKeyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("KEY_REVOKED_EVENT_HANDLER")
@RequiredArgsConstructor
public class KeyRevokedEventHandler implements EventHandler {
    private final AuthorityKeyRepository authorityKeyRepository;

    public void handle(Object obj) {
        log.info("KeyRevokedEventHandler started with {}", obj);
        KeyRevokedEvent e = (KeyRevokedEvent) obj;
        AuthorityKey authorityKey =
                authorityKeyRepository.findOneByIssuerAndKeyId(e.getIssuer(), e.getKeyId()).get();
        authorityKey.setRevoked(true);
        authorityKey.setRevokedAt(e.getRevokedAt());
        log.info("KeyRevokedEventHandler ended with {}", authorityKey);
        authorityKeyRepository.save(authorityKey);
    }
}
