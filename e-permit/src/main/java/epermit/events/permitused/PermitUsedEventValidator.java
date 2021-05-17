package epermit.events.permitused;


import org.springframework.stereotype.Service;
import epermit.events.EventValidationResult;
import epermit.events.EventValidator;
import epermit.repositories.IssuedPermitRepository;
import epermit.utils.GsonUtil;
import lombok.RequiredArgsConstructor;

@Service("PERMIT_USED_VALIDATOR")
@RequiredArgsConstructor
public class PermitUsedEventValidator implements EventValidator {

    private final IssuedPermitRepository issuedPermitRepository;

    @Override
    public EventValidationResult validate(String payload) {
        PermitUsedEvent event = GsonUtil.getGson().fromJson(payload, PermitUsedEvent.class);
        if(!issuedPermitRepository.existsByIssuedForAndPermitId(event.getIssuedFor(), event.getPermitId())){
            return EventValidationResult.fail("INVALID_PERMITID_OR_ISSUER");
        }
        return EventValidationResult.success(event);
    }
}

