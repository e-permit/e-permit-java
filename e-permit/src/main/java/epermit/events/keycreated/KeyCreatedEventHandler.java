package epermit.events.keycreated;

import java.text.ParseException;
import com.nimbusds.jose.jwk.ECKey;
import org.springframework.stereotype.Service;
import epermit.events.EventHandleResult;
import epermit.events.EventHandler;
import epermit.services.AuthorityService;
import epermit.utils.GsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("KEY_CREATED")
@RequiredArgsConstructor
public class KeyCreatedEventHandler implements EventHandler {
    private final AuthorityService authorityService;

    @SneakyThrows
    @Override
    public EventHandleResult handle(String payload) {
        KeyCreatedEvent e = GsonUtil.getGson().fromJson(payload, KeyCreatedEvent.class);
        EventHandleResult validation = validate(e);
        if (!validation.isOk()) {
            return validation;
        }
        if (authorityService.isPublicKeyExist(e.getIssuer(), e.getKeyId())) {
            return EventHandleResult.fail("KEYID_EXIST");
        }
        authorityService.handleKeyCreated(e);
        return EventHandleResult.success();
    }

    private EventHandleResult validate(KeyCreatedEvent e) {
        try {
            ECKey key = ECKey.parse(e.getJwk()).toPublicJWK();
            if (!key.getKeyID().equals(e.getKeyId())) {
                log.info("The key in jwk doesnt match with event");
                return EventHandleResult.fail("INVALID_KID");
            }
        } catch (ParseException ex) {
            log.error(ex.getMessage(), ex);
            return EventHandleResult.fail("INVALID_KEY");
        }
        return EventHandleResult.success();
    }
}
