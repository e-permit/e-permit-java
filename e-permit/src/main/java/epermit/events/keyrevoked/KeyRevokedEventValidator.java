package epermit.events.keyrevoked;

import org.springframework.stereotype.Service;
import epermit.events.EventValidationResult;
import epermit.events.EventValidator;
import java.util.Map;
import epermit.repositories.AuthorityKeyRepository;
import epermit.utils.GsonUtil;
import lombok.RequiredArgsConstructor;

@Service("KEY_REVOKED_EVENT_VALIDATOR")
@RequiredArgsConstructor
public class KeyRevokedEventValidator implements EventValidator {
    private final AuthorityKeyRepository authorityKeyRepository;

    @Override
    public EventValidationResult validate(Map<String, Object> payload) {
        KeyRevokedEvent e = GsonUtil.fromMap(payload, KeyRevokedEvent.class);

        if (!authorityKeyRepository.isPublicKeyExist(e.getIssuer(), e.getKeyId())) {
            return EventValidationResult.fail("KEY_NOTFOUND", e);
        }

        return EventValidationResult.success(e);
    }

}
