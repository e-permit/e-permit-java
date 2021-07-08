package epermit.ledgerevents.keyrevoked;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Service;
import epermit.entities.LedgerPublicKey;
import epermit.ledgerevents.LedgerEventHandleResult;
import epermit.ledgerevents.LedgerEventHandler;
import epermit.repositories.LedgerPublicKeyRepository;
import epermit.utils.GsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("KEY_REVOKED_EVENT_HANDLER")
@RequiredArgsConstructor
public class KeyRevokedLedgerEventHandler implements LedgerEventHandler {
    private final LedgerPublicKeyRepository publicKeyRepository;

    @SneakyThrows
    public LedgerEventHandleResult handle(Map<String, Object> claims) {
        log.info("KeyRevokedLedgerEvent started with {}", claims);
        KeyRevokedLedgerEvent e = GsonUtil.fromMap(claims, KeyRevokedLedgerEvent.class);
        List<LedgerPublicKey> keys =
                publicKeyRepository.findAllByAuthorityCodeAndRevokedFalse(e.getEventIssuer());
        if (keys.size() < 2) {
            log.info("KeyRevokedEventValidator result is THERE_IS_ONLY_ONE_KEY");
            return LedgerEventHandleResult.fail("THERE_IS_ONLY_ONE_KEY");
        }
        Optional<LedgerPublicKey> publicKeyR =
                keys.stream().filter(x -> x.getKeyId().equals(e.getKeyId())).findFirst();
        if (!publicKeyR.isPresent()) {
            log.info("KeyRevokedEventValidator result is KEY_NOTFOUND");
            return LedgerEventHandleResult.fail("KEY_NOTFOUND");
        }
        LedgerPublicKey publicKey = publicKeyR.get();
        publicKey.setRevoked(true);
        publicKey.setRevokedAt(e.getRevokedAt());
        log.info("KeyRevokedEventHandler ended with {}", publicKey);
        publicKeyRepository.save(publicKey);
        return LedgerEventHandleResult.success();
    }
}
