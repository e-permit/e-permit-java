package epermit.events.permitrevoked;

import java.util.Map;
import org.springframework.stereotype.Service;
import epermit.events.EventValidationResult;
import epermit.events.EventValidator;
import epermit.repositories.PermitRepository;
import epermit.utils.GsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("PERMIT_REVOKED_EVENT_VALIDATOR")
@RequiredArgsConstructor
public class PermitRevokedEventValidator implements EventValidator {
    private final PermitRepository permitRepository;

    @SneakyThrows
    public EventValidationResult validate(Map<String, Object> payload) {
        log.info("PermitRevokedEventValidator started with {}", payload);
        PermitRevokedEvent event = GsonUtil.fromMap(payload, PermitRevokedEvent.class);
        if (!permitRepository.existsByIssuerAndPermitId(event.getIssuer(), event.getPermitId())) {
            log.info("PermitRevokedEventValidator result is INVALID_PERMITID_OR_ISSUER");
            return EventValidationResult.fail("INVALID_PERMITID_OR_ISSUER", event);
        }
        log.info("PermitRevokedEventValidator result is succeed");
        return EventValidationResult.success(event);
    }
}

