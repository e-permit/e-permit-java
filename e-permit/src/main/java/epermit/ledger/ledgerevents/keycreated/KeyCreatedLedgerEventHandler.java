package epermit.ledger.ledgerevents.keycreated;

import org.springframework.stereotype.Service;
import epermit.ledger.entities.LedgerPublicKey;
import epermit.ledger.ledgerevents.LedgerEventHandler;
import epermit.ledger.repositories.AuthorityRepository;
import epermit.ledger.repositories.LedgerPublicKeyRepository;
import epermit.ledger.utils.GsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("KEY_CREATED_EVENT_HANDLER")
@RequiredArgsConstructor
public class KeyCreatedLedgerEventHandler implements LedgerEventHandler {
    private final LedgerPublicKeyRepository keyRepository;

    public void handle(Object obj) {
        log.info("KeyCreatedEventHandler started with {}", obj);
        KeyCreatedLedgerEvent e = (KeyCreatedLedgerEvent)obj;
        LedgerPublicKey key = new LedgerPublicKey();
        key.setKeyId(e.getJwk().getKid());
        key.setAuthorityCode(e.getIssuer());
        key.setJwk(GsonUtil.getGson().toJson(e.getJwk()));
        log.info("KeyCreatedEventHandler ended with {}", key.getJwk());
        keyRepository.save(key);
    }
}
