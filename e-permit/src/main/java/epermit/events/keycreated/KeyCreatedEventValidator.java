package epermit.events.keycreated;

import org.springframework.stereotype.Service;
import epermit.events.EventValidationResult;
import epermit.events.EventValidator;

import java.text.ParseException;
import java.util.Map;
import com.nimbusds.jose.jwk.ECKey;
import epermit.repositories.AuthorityKeyRepository;
import epermit.utils.GsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("KEY_CREATED_EVENT_VALIDATOR")
@RequiredArgsConstructor
public class KeyCreatedEventValidator implements EventValidator {
    private final AuthorityKeyRepository authorityKeyRepository;

    @Override
    public EventValidationResult validate(Map<String, Object> payload) {
        KeyCreatedEvent e = GsonUtil.fromMap(payload, KeyCreatedEvent.class);

        try {
            ECKey key = ECKey.parse(e.getJwk()).toPublicJWK();
            if (!key.getKeyID().equals(e.getKeyId())) {
                log.info("The key in jwk doesnt match with event");
                return EventValidationResult.fail("INVALID_KID", e);
            }
        } catch (ParseException ex) {
            log.error(ex.getMessage(), ex);
            return EventValidationResult.fail("INVALID_KEY", e);
        }
        if (authorityKeyRepository.isPublicKeyExist(e.getIssuer(), e.getKeyId())) {
            return EventValidationResult.fail("KEYID_EXIST", e);
        }

        return EventValidationResult.success(e);
    }

}
