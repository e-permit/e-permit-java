package epermit.events.quotacreated;

import org.springframework.stereotype.Service;
import epermit.events.EventValidationResult;
import epermit.events.EventValidator;
import epermit.events.EventHandler;
import epermit.repositories.AuthorityRepository;
import epermit.utils.GsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@Service("QUOTA_CREATED_VALIDATOR")
@RequiredArgsConstructor
public class QuotaCreatedEventValidator implements EventValidator {
    @Override
    public EventValidationResult validate(String payload) {
        QuotaCreatedEvent e = GsonUtil.getGson().fromJson(payload, QuotaCreatedEvent.class);
        return EventValidationResult.success(e);
    }

}
