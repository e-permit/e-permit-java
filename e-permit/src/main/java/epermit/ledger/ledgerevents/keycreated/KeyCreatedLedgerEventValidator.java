package epermit.ledger.ledgerevents.keycreated;

import org.springframework.stereotype.Service;
import epermit.ledger.entities.LedgerPublicKey;
import epermit.ledger.ledgerevents.LedgerEventValidationResult;
import epermit.ledger.ledgerevents.LedgerEventValidator;
import epermit.ledger.repositories.LedgerPublicKeyRepository;
import epermit.ledger.utils.GsonUtil;
import java.text.ParseException;
import java.util.Map;
import java.util.Optional;
import com.nimbusds.jose.jwk.ECKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("KEY_CREATED_EVENT_VALIDATOR")
@RequiredArgsConstructor
public class KeyCreatedLedgerEventValidator implements LedgerEventValidator {
    private final LedgerPublicKeyRepository publicKeyRepository;

    @Override
    public LedgerEventValidationResult validate(Map<String, Object> payload) {
        log.info("KeyCreatedEventValidator started with {}", payload);
        KeyCreatedLedgerEvent e = GsonUtil.fromMap(payload, KeyCreatedLedgerEvent.class);

        try {
            ECKey.parse(GsonUtil.getGson().toJson(e.getJwk())).toPublicJWK();
        } catch (ParseException ex) {
            log.error(ex.getMessage(), ex);
            log.info("KeyCreatedEventValidator result is  INVALID_KEY");
            return LedgerEventValidationResult.fail("INVALID_KEY", e);
        }
        Optional<LedgerPublicKey> pubKey = publicKeyRepository
                .findOneByAuthorityCodeAndKeyId(e.getIssuer(), e.getJwk().getKid());
        if (pubKey.isPresent()) {
            log.info("KeyCreatedEventValidator result is  KEYID_EXIST");
            return LedgerEventValidationResult.fail("KEYID_EXIST", e);
        }
        log.info("KeyCreatedEventValidator result is  succeed");
        return LedgerEventValidationResult.success(e);
    }

}
