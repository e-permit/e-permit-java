package epermit.ledgerevents.keycreated;

import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import epermit.entities.LedgerPublicKey;
import epermit.ledgerevents.LedgerEventHandleResult;
import epermit.ledgerevents.LedgerEventHandler;
import epermit.models.dtos.PublicJwk;
import epermit.repositories.LedgerPublicKeyRepository;
import epermit.utils.GsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("KEY_CREATED_EVENT_HANDLER")
@RequiredArgsConstructor
public class KeyCreatedLedgerEventHandler implements LedgerEventHandler {
    private final LedgerPublicKeyRepository keyRepository;
    private final ModelMapper modelMapper;
    private final Validator validator;

    @SneakyThrows
    public LedgerEventHandleResult handle(Object obj) {
        log.info("KeyCreatedEventHandler started with {}", obj);
        KeyCreatedLedgerEvent e = (KeyCreatedLedgerEvent) obj;
        Set<ConstraintViolation<KeyCreatedLedgerEvent>> violations = validator.validate(e);
        if (!violations.isEmpty()) {
            log.info("KeyCreatedEventValidator result is  VALIDATION_ERROR {}", violations);
            return LedgerEventHandleResult.fail("KEYID_EXIST");
        }
        boolean keyExist = keyRepository.existsByAuthorityCodeAndKeyId(e.getIssuer(), e.getKid());
        if (keyExist) {
            log.info("KeyCreatedEventValidator result is  KEYID_EXIST");
            return LedgerEventHandleResult.fail("KEYID_EXIST");
        }

        LedgerPublicKey key = new LedgerPublicKey();
        key.setKeyId(e.getKid());
        key.setAuthorityCode(e.getIssuer());
        key.setJwk(GsonUtil.getGson().toJson(modelMapper.map(e, PublicJwk.class)));
        log.info("KeyCreatedEventHandler ended with {}", key.getJwk());
        keyRepository.save(key);
        return LedgerEventHandleResult.success();
    }
}
