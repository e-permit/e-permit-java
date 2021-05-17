package epermit.events.permitrevoked;

import org.springframework.stereotype.Service;
import epermit.events.EventValidationResult;
import epermit.events.EventValidator;
import epermit.repositories.PermitRepository;
import epermit.utils.GsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@Service("PERMIT_REVOKED_VALIDATOR")
@RequiredArgsConstructor
public class PermitRevokedEventValidator implements EventValidator {
    private final PermitRepository permitRepository;
    @SneakyThrows
    public EventValidationResult validate(String payload) {
        PermitRevokedEvent event = GsonUtil.getGson().fromJson(payload, PermitRevokedEvent.class);      
        if(!permitRepository.existsByIssuerAndPermitId(event.getIssuer(), event.getPermitId())){
            return EventValidationResult.fail("INVALID_PERMITID_OR_ISSUER");
        }
        return EventValidationResult.success(event);
    }
}

