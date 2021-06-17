package epermit.ledgerevents.keyrevoked;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import epermit.entities.LedgerPublicKey;
import epermit.ledgerevents.LedgerEventHandleResult;
import epermit.ledgerevents.LedgerEventHandler;
import epermit.repositories.LedgerPublicKeyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("KEY_REVOKED_EVENT_HANDLER")
@RequiredArgsConstructor
public class KeyRevokedLedgerEventHandler implements LedgerEventHandler {
    private final LedgerPublicKeyRepository publicKeyRepository;

    public LedgerEventHandleResult handle(Object obj) {
        log.info("KeyRevokedEventHandler started with {}", obj);
        KeyRevokedLedgerEvent e = (KeyRevokedLedgerEvent) obj;
        List<LedgerPublicKey> keys = publicKeyRepository.findAllByAuthorityCodeAndRevokedFalse(e.getIssuer());
        if (keys.size() < 2) {
            log.info("KeyRevokedEventValidator result is THERE_IS_ONLY_ONE_KEY");
            return LedgerEventHandleResult.fail("THERE_IS_ONLY_ONE_KEY");
        }
        if (!keys.stream().anyMatch(x -> x.getKeyId().equals(e.getKeyId()))) {
            log.info("KeyRevokedEventValidator result is KEY_NOTFOUND");
            return LedgerEventHandleResult.fail("KEY_NOTFOUND");
        }
        LedgerPublicKey publicKey =
                keys.stream().filter(x -> x.getKeyId().equals(e.getKeyId())).findFirst().get();
        publicKey.setRevoked(true);
        publicKey.setRevokedAt(e.getRevokedAt());
        log.info("KeyRevokedEventHandler ended with {}", publicKey);
        publicKeyRepository.save(publicKey);
        return LedgerEventHandleResult.success();
    }
}
