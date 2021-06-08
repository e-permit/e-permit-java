package epermit.events.permitused;


import java.util.Map;
import org.springframework.stereotype.Service;
import epermit.events.EventValidationResult;
import epermit.events.EventValidator;
import epermit.repositories.IssuedPermitRepository;
import epermit.utils.GsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("PERMIT_USED_EVENT_VALIDATOR")
@RequiredArgsConstructor
public class PermitUsedEventValidator implements EventValidator {

    private final IssuedPermitRepository issuedPermitRepository;

    @Override
    public EventValidationResult validate(Map<String, Object> payload) {
        PermitUsedEvent event = GsonUtil.fromMap(payload, PermitUsedEvent.class);
        if(!issuedPermitRepository.existsByIssuedForAndPermitId(event.getIssuedFor(), event.getPermitId())){
            log.info("PermitUsedEventValidator result is INVALID_PERMITID_OR_ISSUER event is {}", event);
            return EventValidationResult.fail("INVALID_PERMITID_OR_ISSUER", event);
        }
        log.info("PermitUsedEventValidator result is succeed");
        return EventValidationResult.success(event);
    }
}

