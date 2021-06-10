package epermit.events.keyrevoked;

import org.springframework.stereotype.Service;
import epermit.entities.Authority;
import epermit.entities.AuthorityKey;
import epermit.events.EventValidationResult;
import epermit.events.EventValidator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import epermit.repositories.AuthorityRepository;
import epermit.utils.GsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("KEY_REVOKED_EVENT_VALIDATOR")
@RequiredArgsConstructor
public class KeyRevokedEventValidator implements EventValidator {
    private final AuthorityRepository authorityRepository;

    @Override
    public EventValidationResult validate(Map<String, Object> payload) {
        log.info("KeyRevokedEventValidator started with {}", payload);
        KeyRevokedEvent e = GsonUtil.fromMap(payload, KeyRevokedEvent.class);
        Authority authority = authorityRepository.findOneByCode(e.getIssuer());
        List<AuthorityKey> keys = authority.getKeys();
        if(keys.stream().filter(x-> !x.isRevoked()).count() < 2){
            log.info("KeyRevokedEventValidator result is THERE_IS_ONLY_ONE_KEY");
            return EventValidationResult.fail("THERE_IS_ONLY_ONE_KEY", e);
        }
        if (!keys.stream().anyMatch(x -> x.getKeyId().equals(e.getKeyId()))) {
            log.info("KeyRevokedEventValidator result is KEY_NOTFOUND");
            return EventValidationResult.fail("KEY_NOTFOUND", e);
        }
        log.info("KeyRevokedEventValidator result is succeed");
        return EventValidationResult.success(e);
    }

}
