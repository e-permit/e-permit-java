package epermit.events.quotacreated;

import java.util.Map;
import org.springframework.stereotype.Service;
import epermit.entities.Authority;
import epermit.events.EventValidationResult;
import epermit.events.EventValidator;
import epermit.repositories.AuthorityRepository;
import epermit.utils.GsonUtil;
import lombok.RequiredArgsConstructor;

@Service("QUOTA_CREATED_EVENT_VALIDATOR")
@RequiredArgsConstructor
public class QuotaCreatedEventValidator implements EventValidator {
    private final AuthorityRepository authorityRepository;

    @Override
    public EventValidationResult validate(Map<String, Object> payload) {
        QuotaCreatedEvent e = GsonUtil.fromMap(payload, QuotaCreatedEvent.class);
        Authority authority = authorityRepository.findOneByCode(e.getIssuer());
        if (authority.getVerifierQuotas().stream()
                .anyMatch(x -> x.isActive() && x.getPermitType() == e.getPermitType()
                        && x.getPermitYear() == e.getPermitYear())) {
            return EventValidationResult.fail("QUOTA_ALREADY_EXIST", e);
        }
        return EventValidationResult.success(e);
    }

}
