package epermit.events.permitcreated;

import java.util.Map;
import org.springframework.stereotype.Service;
import epermit.events.EventValidationResult;
import epermit.events.EventValidator;
import epermit.repositories.PermitRepository;
import epermit.utils.GsonUtil;
import epermit.utils.PermitUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("PERMIT_CREATED_EVENT_VALIDATOR")
@RequiredArgsConstructor
public class PermitCreatedEventValidator implements EventValidator {
    private final PermitRepository permitRepository;
    private final PermitUtil permitUtil;

    public EventValidationResult validate(Map<String, Object> payload) {
        log.info("PermitCreatedEventValidator started with {}", payload);
        PermitCreatedEvent event = GsonUtil.fromMap(payload, PermitCreatedEvent.class);
        String expectedPermitId = permitUtil.getPermitId(event.getIssuer(), event.getIssuedFor(),
                event.getPermitType(), event.getPermitYear(), event.getSerialNumber());
        if (!expectedPermitId.equals(event.getPermitId())) {
            log.info("PermitCreatedEventValidator result is INVALID_PERMITID");
            return EventValidationResult.fail("INVALID_PERMITID", event);
        }
        boolean exist =
                permitRepository.existsByIssuerAndPermitId(event.getIssuer(), event.getPermitId());
        if (exist) {
            log.info("PermitCreatedEventValidator result is PERMIT_EXIST");
            return EventValidationResult.fail("PERMIT_EXIST", event);
        }

        if (!permitUtil.isQuotaSufficient(event.getIssuer(), event.getPermitYear(),
                event.getSerialNumber(), event.getPermitType())) {
            log.info("PermitCreatedEventValidator result is INSUFFICIENT_QUOTA");
            return EventValidationResult.fail("INSUFFICIENT_QUOTA", event);
        }
        log.info("PermitCreatedEventValidator is succeed");
        return EventValidationResult.success(event);
    }
}


