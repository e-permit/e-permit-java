package epermit.ledger.ledgerevents.keycreated;

import java.text.ParseException;
import com.nimbusds.jose.jwk.ECKey;
import org.springframework.stereotype.Service;
import epermit.ledger.entities.LedgerPublicKey;
import epermit.ledger.ledgerevents.LedgerEventHandleResult;
import epermit.ledger.ledgerevents.LedgerEventHandler;
import epermit.ledger.repositories.LedgerPublicKeyRepository;
import epermit.ledger.utils.GsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("KEY_CREATED_EVENT_HANDLER")
@RequiredArgsConstructor
public class KeyCreatedLedgerEventHandler implements LedgerEventHandler {
    private final LedgerPublicKeyRepository keyRepository;

    @SneakyThrows
    public LedgerEventHandleResult handle(Object obj) {
        log.info("KeyCreatedEventHandler started with {}", obj);
        KeyCreatedLedgerEvent e = (KeyCreatedLedgerEvent) obj;
        boolean keyExist =
                keyRepository.existsByAuthorityCodeAndKeyId(e.getIssuer(), e.getJwk().getKid());
        if (keyExist) {
            log.info("KeyCreatedEventValidator result is  KEYID_EXIST");
            return LedgerEventHandleResult.fail("KEYID_EXIST");
        }
        
        try {
            ECKey.parse(GsonUtil.getGson().toJson(e.getJwk())).toPublicJWK();
        } catch (ParseException ex) {
            log.error(ex.getMessage(), ex);
            log.info("KeyCreatedEventValidator result is  INVALID_KEY");
            return LedgerEventHandleResult.fail("INVALID_KEY");
        }
        LedgerPublicKey key = new LedgerPublicKey();
        key.setKeyId(e.getJwk().getKid());
        key.setAuthorityCode(e.getIssuer());
        key.setJwk(GsonUtil.getGson().toJson(e.getJwk()));
        log.info("KeyCreatedEventHandler ended with {}", key.getJwk());
        keyRepository.save(key);
        return LedgerEventHandleResult.success();
    }
}
