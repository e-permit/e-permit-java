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
        log.info("KeyCreatedEventValidator started with {}", payload);
        KeyCreatedEvent e = GsonUtil.fromMap(payload, KeyCreatedEvent.class);

        try {
            ECKey.parse(GsonUtil.getGson().toJson(e.getJwk())).toPublicJWK();
        } catch (ParseException ex) {
            log.error(ex.getMessage(), ex);
            log.info("KeyCreatedEventValidator result is  INVALID_KEY");
            return EventValidationResult.fail("INVALID_KEY", e);
        }
        if (authorityKeyRepository.isPublicKeyExist(e.getIssuer(), e.getJwk().getKid())) {
            log.info("KeyCreatedEventValidator result is  KEYID_EXIST");
            return EventValidationResult.fail("KEYID_EXIST", e);
        }
        log.info("KeyCreatedEventValidator result is  succeed");
        return EventValidationResult.success(e);
    }

}
