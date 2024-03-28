package epermit.ledgerevents.keyrevoked;

import java.util.List;
import org.springframework.stereotype.Service;

import epermit.commons.EpermitValidationException;
import epermit.commons.ErrorCodes;
import epermit.entities.LedgerPublicKey;
import epermit.ledgerevents.LedgerEventBase;
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
    public <T extends LedgerEventBase> void handle(T claims) {
        log.info("KeyRevokedLedgerEvent started with {}", claims);
        KeyRevokedLedgerEvent e = (KeyRevokedLedgerEvent) claims;
        List<LedgerPublicKey> keys = publicKeyRepository.findAllByPartnerAndRevokedFalse(e.getEventProducer());
        if (keys.size() < 2)
            throw new EpermitValidationException(ErrorCodes.INSUFFICIENT_KEY);
        LedgerPublicKey publicKey = keys.stream().filter(x -> x.getKeyId().equals(e.getKeyId())).findFirst()
                .orElseThrow(() -> new EpermitValidationException(ErrorCodes.KEY_NOTFOUND));

        publicKey.setRevoked(true);
        publicKey.setRevokedAt(e.getRevokedAt());
        log.info("KeyRevokedEventHandler ended with {}", publicKey);
        publicKeyRepository.save(publicKey);
    }
}
