package epermit.ledgerevents.keycreated;

import java.util.Map;
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

    @SneakyThrows
    public LedgerEventHandleResult handle(Map<String, Object> claims) {
        log.info("KeyCreatedEventHandler started with {}", claims);
        KeyCreatedLedgerEvent e = GsonUtil.fromMap(claims, KeyCreatedLedgerEvent.class);
        boolean keyExist = keyRepository.existsByAuthorityCodeAndKeyId(e.getEventIssuer(), e.getKid());
        if (keyExist) {
            log.info("KeyCreatedEventValidator result is  KEYID_EXIST");
            return LedgerEventHandleResult.fail("KEYID_EXIST");
        }

        LedgerPublicKey key = new LedgerPublicKey();
        key.setKeyId(e.getKid());
        key.setAuthorityCode(e.getEventIssuer());
        key.setJwk(GsonUtil.getGson().toJson(modelMapper.map(e, PublicJwk.class)));
        log.info("KeyCreatedEventHandler ended with {}", key.getJwk());
        keyRepository.save(key);
        return LedgerEventHandleResult.success();
    }
}
