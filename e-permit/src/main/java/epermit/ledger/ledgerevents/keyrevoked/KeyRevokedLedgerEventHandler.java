package epermit.ledger.ledgerevents.keyrevoked;

import org.springframework.stereotype.Service;
import epermit.ledger.entities.LedgerPublicKey;
import epermit.ledger.ledgerevents.LedgerEventHandler;
import epermit.ledger.repositories.LedgerPublicKeyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("KEY_REVOKED_EVENT_HANDLER")
@RequiredArgsConstructor
public class KeyRevokedLedgerEventHandler implements LedgerEventHandler {
    private final LedgerPublicKeyRepository publicKeyRepository;

    public void handle(Object obj) {
        log.info("KeyRevokedEventHandler started with {}", obj);
        KeyRevokedLedgerEvent e = (KeyRevokedLedgerEvent) obj;
        LedgerPublicKey publicKey = publicKeyRepository
                .findOneByAuthorityCodeAndKeyId(e.getIssuer(), e.getKeyId()).get();

        publicKey.setRevoked(true);
        publicKey.setRevokedAt(e.getRevokedAt());
        log.info("KeyRevokedEventHandler ended with {}", publicKey);
        publicKeyRepository.save(publicKey);
    }
}
