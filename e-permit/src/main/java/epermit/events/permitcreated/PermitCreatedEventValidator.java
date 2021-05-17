package epermit.events.permitcreated;

import com.google.gson.Gson;
import org.springframework.stereotype.Service;
import epermit.events.EventValidationResult;
import epermit.events.EventValidator;
import epermit.repositories.PermitRepository;
import epermit.utils.GsonUtil;
import epermit.utils.PermitUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("PERMIT_CREATED_VALIDATOR")
@RequiredArgsConstructor
public class PermitCreatedEventValidator implements EventValidator {
    private final PermitRepository permitRepository;
    private final PermitUtil permitUtil;

    public EventValidationResult validate(String payload) {
        Gson gson = GsonUtil.getGson();
        PermitCreatedEvent event = gson.fromJson(payload, PermitCreatedEvent.class);
        String expectedPermitId = permitUtil.getPermitId(event.getIssuer(), event.getIssuedFor(),
                event.getPermitType(), event.getPermitYear(), event.getSerialNumber());
        if (!expectedPermitId.equals(event.getPermitId())) {
            log.info("INVALID_PERMITID");
            return EventValidationResult.fail("INVALID_PERMITID");
        }
        boolean exist = permitRepository.existsByIssuerAndPermitId(event.getIssuer(),
                event.getPermitId());
        if (exist) {
            log.info("PERMIT_EXIST");
            return EventValidationResult.fail("PERMIT_EXIST");
        }

        if (!permitUtil.isQuotaSufficient(event.getIssuer(), event.getPermitYear(),
                event.getSerialNumber(), event.getPermitType())) {
            log.info("QUOTA_DOESNT_MATCH");
            return EventValidationResult.fail("QUOTA_DOESNT_MATCH");
        }
        return EventValidationResult.success(event);
    }
}


