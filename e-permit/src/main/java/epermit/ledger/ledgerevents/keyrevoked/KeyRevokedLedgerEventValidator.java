package epermit.ledger.ledgerevents.keyrevoked;

import org.springframework.stereotype.Service;
import epermit.ledger.entities.LedgerPublicKey;
import epermit.ledger.ledgerevents.LedgerEventValidationResult;
import epermit.ledger.ledgerevents.LedgerEventValidator;
import epermit.ledger.repositories.LedgerPublicKeyRepository;
import epermit.ledger.utils.GsonUtil;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("KEY_REVOKED_EVENT_VALIDATOR")
@RequiredArgsConstructor
public class KeyRevokedLedgerEventValidator implements LedgerEventValidator {
    private final LedgerPublicKeyRepository publicKeyRepository;

    @Override
    public LedgerEventValidationResult validate(Map<String, Object> payload) {
        log.info("KeyRevokedEventValidator started with {}", payload);
        KeyRevokedLedgerEvent e = GsonUtil.fromMap(payload, KeyRevokedLedgerEvent.class);
        List<LedgerPublicKey> keys = publicKeyRepository.findAllByAuthorityCode(e.getIssuer());
        if(keys.stream().filter(x-> !x.isRevoked()).count() < 2){
            log.info("KeyRevokedEventValidator result is THERE_IS_ONLY_ONE_KEY");
            return LedgerEventValidationResult.fail("THERE_IS_ONLY_ONE_KEY", e);
        }
        if (!keys.stream().anyMatch(x -> x.getKeyId().equals(e.getKeyId()))) {
            log.info("KeyRevokedEventValidator result is KEY_NOTFOUND");
            return LedgerEventValidationResult.fail("KEY_NOTFOUND", e);
        }
        log.info("KeyRevokedEventValidator result is succeed");
        return LedgerEventValidationResult.success(e);
    }

}
