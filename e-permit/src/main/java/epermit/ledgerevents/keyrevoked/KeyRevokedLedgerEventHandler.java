package epermit.ledgerevents.keyrevoked;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Service;
import epermit.commons.Check;
import epermit.commons.ErrorCodes;
import epermit.commons.GsonUtil;
import epermit.entities.LedgerPublicKey;
import epermit.ledgerevents.LedgerEventHandler;
import epermit.repositories.LedgerPublicKeyRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("KEY_REVOKED_EVENT_HANDLER")
@RequiredArgsConstructor
public class KeyRevokedLedgerEventHandler implements LedgerEventHandler {
    private final LedgerPublicKeyRepository publicKeyRepository;

    @SneakyThrows
    public void handle(Map<String, Object> claims) {
        log.info("KeyRevokedLedgerEvent started with {}", claims);
        KeyRevokedLedgerEvent e = GsonUtil.fromMap(claims, KeyRevokedLedgerEvent.class);
        List<LedgerPublicKey> keys =
                publicKeyRepository.findAllByAuthorityCodeAndRevokedFalse(e.getEventProducer());
        Check.assertTrue(keys.size() > 1, ErrorCodes.INSUFFICIENT_KEY);
        Optional<LedgerPublicKey> publicKeyR =
                keys.stream().filter(x -> x.getKeyId().equals(e.getKeyId())).findFirst();
        Check.assertTrue(publicKeyR.isPresent(), ErrorCodes.KEY_NOTFOUND);
        LedgerPublicKey publicKey = publicKeyR.get();
        publicKey.setRevoked(true);
        publicKey.setRevokedAt(e.getRevokedAt());
        log.info("KeyRevokedEventHandler ended with {}", publicKey);
        publicKeyRepository.save(publicKey);
    }
}
